
package af.asr.customer.api;

import af.asr.customer.catalog.service.FieldValueValidator;
import af.asr.customer.domain.*;
import af.asr.customer.model.PortraitEntity;
import af.asr.customer.service.CustomerService;
import af.asr.customer.service.TaskService;
import af.asr.infrastructure.service.UserService;
import af.gov.anar.lang.infrastructure.exception.service.ServiceException;
import af.gov.anar.lang.validation.constraints.ValidIdentifier;
import af.gov.anar.lib.logger.appender.ConsoleAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/")
public class CustomerRestController {

  private final ConsoleAppender logger = new ConsoleAppender();
  private final CustomerService customerService;
  private final FieldValueValidator fieldValueValidator;
  private final TaskService taskService;
  private final Environment environment;
  private final UserService userService;

  @Autowired
  public CustomerRestController(final CustomerService customerService,
                                final FieldValueValidator fieldValueValidator,
                                final TaskService taskService,
                                final Environment environment, UserService userService) {
    super();
    this.customerService = customerService;
    this.fieldValueValidator = fieldValueValidator;
    this.taskService = taskService;
    this.environment = environment;
    this.userService = userService;
  }

//  @Permittable(value = AcceptedTokenType.SYSTEM)
  @RequestMapping(
      value = "/initialize",
      method = RequestMethod.POST,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Void>
  initialize() throws InterruptedException {
    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> createCustomer(@RequestBody @Valid final Customer customer) throws InterruptedException {
    if (this.customerService.customerExists(customer.getIdentifier())) {
      throw ServiceException.conflict("Customer {0} already exists.", customer.getIdentifier());
    }

    if (customer.getCustomValues() != null) {
      this.fieldValueValidator.validateValues(customer.getCustomValues());
    }

    this.customerService.createCustomer(customer);
    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<CustomerPage> fetchCustomers(@RequestParam(value = "term", required = false) final String term,
                                              @RequestParam(value = "includeClosed", required = false) final Boolean includeClosed,
                                              @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                                              @RequestParam(value = "size", required = false) final Integer size,
                                              @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                                              @RequestParam(value = "sortDirection", required = false) final String sortDirection) {
    return ResponseEntity.ok(this.customerService.fetchCustomer(
        term, (includeClosed != null ? includeClosed : Boolean.FALSE),
        this.createPageRequest(pageIndex, size, sortColumn, sortDirection)));
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers/{identifier}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Customer> findCustomer(@PathVariable("identifier") final String identifier) {
    final Optional<Customer> customer = this.customerService.findCustomer(identifier);
    if (customer.isPresent()) {
      return ResponseEntity.ok(customer.get());
    } else {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers/{identifier}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> updateCustomer(@PathVariable("identifier") final String identifier,
                                      @RequestBody final Customer customer) {
    if (this.customerService.customerExists(identifier)) {
      if (customer.getCustomValues() != null) {
        this.fieldValueValidator.validateValues(customer.getCustomValues());
      }
      this.customerService.updateCustomer(customer);
    } else {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }
    return ResponseEntity.accepted().build();
  }

  @RequestMapping(
      value = "/customers/{identifier}/commands",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> customerCommand(@PathVariable("identifier") final String identifier,
                                       @RequestBody final Command command) {
    final Optional<Customer> customerOptional = this.customerService.findCustomer(identifier);
    if (customerOptional.isPresent()) {
      final Customer customer = customerOptional.get();
      final Command.Action action = Command.Action.valueOf(command.getAction());
      final String currentState = customer.getCurrentState();
      switch (action) {
        case ACTIVATE:
          if (Customer.State.PENDING.name().equals(currentState)) {
            this.customerService.activateCustomer(identifier, command.getComment());
          }
          break;
        case LOCK:
          if (Customer.State.ACTIVE.name().equals(currentState)) {
            this.customerService.lockCustomer(identifier, command.getComment());
          }
          break;
        case UNLOCK:
          if (Customer.State.LOCKED.name().equals(currentState)) {
            this.customerService.unlockCustomer(identifier, command.getComment());
          }
          break;
        case CLOSE:
          if (Customer.State.ACTIVE.name().equals(currentState)
              || Customer.State.LOCKED.name().equals(currentState)
              || Customer.State.PENDING.name().equals(currentState)) {
            this.customerService.closeCustomer(identifier, command.getComment());
          }
          break;
        case REOPEN:
          if (Customer.State.CLOSED.name().equals(currentState)) {
            this.customerService.reopenCustomer(identifier, command.getComment());
          }
          break;
        default:
          throw ServiceException.badRequest("Unsupported action {0}.", command.getAction());
      }
    } else {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }
    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers/{identifier}/commands",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<Command>> fetchCustomerCommands(@PathVariable("identifier") final String identifier) {
    if (this.customerService.customerExists(identifier)) {
      return ResponseEntity.ok(this.customerService.fetchCommandsByCustomer(identifier).collect(Collectors.toList()));
    } else {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers/{identifier}/tasks/{taskIdentifier}",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> addTaskToCustomer(@PathVariable("identifier") final String identifier,
                                         @PathVariable("taskIdentifier") final String taskIdentifier) {
    if (this.customerService.customerExists(identifier)) {
      if (this.taskService.taskDefinitionExists(taskIdentifier)) {
        this.taskService.addTaskToCustomer(identifier, taskIdentifier);
      } else {
        throw ServiceException.notFound("Task definition {0} not found.", taskIdentifier);
      }
    } else {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }
    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers/{identifier}/tasks/{taskIdentifier}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> taskForCustomerExecuted(@PathVariable("identifier") final String identifier,
                                               @PathVariable("taskIdentifier") final String taskIdentifier) {
    final Optional<Customer> optionalCustomer = this.customerService.findCustomer(identifier);
    if (optionalCustomer.isPresent()) {
      final Customer customer = optionalCustomer.get();
      final Optional<TaskDefinition> optionalTaskDefinition = this.taskService.findByIdentifier(taskIdentifier);
      if (optionalTaskDefinition.isPresent()) {
        final TaskDefinition taskDefinition = optionalTaskDefinition.get();
        switch (TaskDefinition.Type.valueOf(taskDefinition.getType())) {
          case ID_CARD:
            final Stream<IdentificationCard> identificationCards = this.customerService.fetchIdentificationCardsByCustomer(identifier);
            if (!identificationCards.findAny().isPresent()) {
              throw ServiceException.conflict("No identification cards for customer found.");
            }
            break;
          case FOUR_EYES:
            if (customer.getCreatedBy().equals(userService.getPreferredUsername())) {
              throw ServiceException.conflict("Signing user must be different than creator.");
            }
            break;
        }
        this.taskService.executeTaskForCustomer(identifier, taskIdentifier);
      } else {
        throw ServiceException.notFound("Task definition {0} not found.", taskIdentifier);
      }
    } else {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }
    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers/{identifier}/tasks",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<TaskDefinition>> findTasksForCustomer(@PathVariable("identifier") final String identifier,
                                                            @RequestParam(value = "includeExecuted", required = false) final Boolean includeExecuted) {
    if (this.customerService.customerExists(identifier)) {
      return ResponseEntity.ok(this.taskService.findTasksByCustomer(identifier, (includeExecuted != null ? includeExecuted : Boolean.FALSE)));
    } else {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers/{identifier}/address",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> putAddress(@PathVariable("identifier") final String identifier,
                                  @RequestBody @Valid final Address address) {
    if (this.customerService.customerExists(identifier)) {
      this.customerService.updateAddress(identifier, address);
    } else {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }
    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers/{identifier}/contact",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> putContactDetails(@PathVariable("identifier") final String identifier,
                                         @RequestBody final List<ContactDetail> contactDetails) {
    if (this.customerService.customerExists(identifier)) {
      this.customerService.updateContactDetails(identifier, contactDetails);
    } else {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }
    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/customers/{identifier}/identifications",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public @ResponseBody
  ResponseEntity<List<IdentificationCard>> fetchIdentificationCards(@PathVariable("identifier") final String identifier) {
    this.throwIfCustomerNotExists(identifier);
    return ResponseEntity.ok(this.customerService.fetchIdentificationCardsByCustomer(identifier).collect(Collectors.toList()));
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<IdentificationCard> findIdentificationCard(@PathVariable("identifier") final String identifier,
                                                            @PathVariable("number") final String number) {
    this.throwIfCustomerNotExists(identifier);

    final Optional<IdentificationCard> identificationCard = this.customerService.findIdentificationCard(number);
    if (identificationCard.isPresent()) {
      return ResponseEntity.ok(identificationCard.get());
    } else {
      throw ServiceException.notFound("Identification card {0} not found.", number);
    }
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/customers/{identifier}/identifications",
          method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> createIdentificationCard(@PathVariable("identifier") final String identifier,
                                                @RequestBody @Valid final IdentificationCard identificationCard) {
    if (this.customerService.customerExists(identifier)) {
      if (this.customerService.identificationCardExists(identificationCard.getNumber())) {
        throw ServiceException.conflict("IdentificationCard {0} already exists.", identificationCard.getNumber());
      }

      this.customerService.createIdentificationCard(identifier, identificationCard);
    } else {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }

    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
      value = "/customers/{identifier}/identifications/{number}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> updateIdentificationCard(@PathVariable("identifier") final String identifier,
                                                @PathVariable("number") final String number,
                                                @RequestBody @Valid final IdentificationCard identificationCard) {
    this.throwIfCustomerNotExists(identifier);
    this.throwIfIdentificationCardNotExists(number);

    if(!number.equals(identificationCard.getNumber())) {
      throw ServiceException.badRequest("Number in path is different from number in request body");
    }

    this.customerService.updateIdentificationCard(identifier, identificationCard.getNumber(), identificationCard);

    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}",
          method = RequestMethod.DELETE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> deleteIdentificationCard(@PathVariable("identifier") final String identifier,
                                                @PathVariable("number") final String number) throws IOException {
    this.throwIfCustomerNotExists(identifier);

    this.customerService.deleteIdentificationCard(number);

    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}/scans",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<IdentificationCardScan>> fetchIdentificationCardScans(@PathVariable("identifier") final String identifier,
                                                                            @PathVariable("number") final String number) {
    this.throwIfCustomerNotExists(identifier);
    this.throwIfIdentificationCardNotExists(number);

    final List<IdentificationCardScan> identificationCardScans = this.customerService.fetchScansByIdentificationCard(number);

    return ResponseEntity.ok(identificationCardScans);
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}/scans/{scanIdentifier}",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<IdentificationCardScan> findIdentificationCardScan(@PathVariable("identifier") final String identifier,
                                                                    @PathVariable("number") final String number,
                                                                    @PathVariable("scanIdentifier") final String scanIdentifier) {
    this.throwIfCustomerNotExists(identifier);
    this.throwIfIdentificationCardNotExists(number);

    final Optional<IdentificationCardScan> identificationCardScan = this.customerService.findIdentificationCardScan(number, scanIdentifier);

    return identificationCardScan
            .map(ResponseEntity::ok)
            .orElseThrow(() -> ServiceException.notFound("Identification card scan {0} not found.", number));
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}/scans/{scanIdentifier}/image",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<byte[]> fetchIdentificationCardScanImage(@PathVariable("identifier") final String identifier,
                                                          @PathVariable("number") final String number,
                                                          @PathVariable("scanIdentifier") final String scanIdentifier) {
    this.throwIfCustomerNotExists(identifier);
    this.throwIfIdentificationCardNotExists(number);
    this.throwIfIdentificationCardScanNotExists(number, scanIdentifier);

    final Optional<byte[]> image = this.customerService.findIdentificationCardScanImage(number, scanIdentifier);

    return image.map(ResponseEntity::ok)
            .orElseThrow(() -> ServiceException.notFound("Identification card scan {0} not found.", number));
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}/scans",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> postIdentificationCardScan(@PathVariable("identifier") final String identifier,
                                                  @PathVariable("number") final String number,
                                                  @RequestParam("scanIdentifier") @ValidIdentifier final String scanIdentifier,
                                                  @RequestParam("description") @Size(max = 4096) final String description,
                                                  @RequestBody final MultipartFile image) throws Exception {
    this.throwIfCustomerNotExists(identifier);
    this.throwIfIdentificationCardNotExists(number);
    this.throwIfInvalidSize(image.getSize());
    this.throwIfInvalidContentType(image.getContentType());

    if (this.customerService.identificationCardScanExists(number, scanIdentifier)) {
      throw ServiceException.conflict("Scan {0} already exists.", scanIdentifier);
    }

    final IdentificationCardScan scan = new IdentificationCardScan();
    scan.setIdentifier(scanIdentifier);
    scan.setDescription(description);

    this.customerService.createIdentificationCardScan(number, scan, image);

    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}/scans/{scanIdentifier}",
          method = RequestMethod.DELETE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> deleteScan(@PathVariable("identifier") final String identifier,
                                  @PathVariable("number") final String number,
                                  @PathVariable("scanIdentifier") final String scanIdentifier) {
    throwIfCustomerNotExists(identifier);
    throwIfIdentificationCardNotExists(number);

    this.customerService.deleteIdentificationCardScan(number, scanIdentifier);

    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.PORTRAIT)
  @RequestMapping(
      value = "/customers/{identifier}/portrait",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE
  )
  public ResponseEntity<byte[]> getPortrait(@PathVariable("identifier") final String identifier) {
    final PortraitEntity portrait = this.customerService.findPortrait(identifier)
        .orElseThrow(() -> ServiceException.notFound("Portrait for Customer ''{0}'' not found.", identifier));

    return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(portrait.getContentType()))
            .contentLength(portrait.getImage().length)
            .body(portrait.getImage());
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.PORTRAIT)
  @RequestMapping(
      value = "/customers/{identifier}/portrait",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public @ResponseBody
  ResponseEntity<Void> postPortrait(@PathVariable("identifier") final String identifier,
                                    @RequestBody final MultipartFile portrait) throws IOException {
    if(portrait == null) {
      throw ServiceException.badRequest("Portrait not found");
    }

    this.throwIfCustomerNotExists(identifier);
    this.throwIfInvalidSize(portrait.getSize());
    this.throwIfInvalidContentType(portrait.getContentType());

    try {
      this.customerService.deletePortrait(identifier);
    } catch (Throwable e) {
//      logger.warn("Could not delete portrait: {0}", e.getMessage());
    }

    this.customerService.createPortrait(identifier, portrait);

    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.PORTRAIT)
  @RequestMapping(
      value = "/customers/{identifier}/portrait",
      method = RequestMethod.DELETE,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.ALL_VALUE
  )
  public @ResponseBody
  ResponseEntity<Void> deletePortrait(@PathVariable("identifier") final String identifier) throws IOException {
    this.customerService.deletePortrait(identifier);

    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.TASK)
  @RequestMapping(
      value = "/tasks",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> createTask(@RequestBody final TaskDefinition taskDefinition) {
    if (this.taskService.taskDefinitionExists(taskDefinition.getIdentifier())) {
      throw ServiceException.conflict("Task definition {0} already exists.", taskDefinition.getIdentifier());
    } else {
      this.taskService.createTaskDefinition(taskDefinition);
    }
    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.TASK)
  @RequestMapping(
      value = "/tasks",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<TaskDefinition>> fetchAllTasks() {
    return ResponseEntity.ok(this.taskService.fetchAll());
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.TASK)
  @RequestMapping(
      value = "/tasks/{identifier}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<TaskDefinition> findTask(@PathVariable("identifier") final String identifier) {
    final Optional<TaskDefinition> taskDefinitionOptional = this.taskService.findByIdentifier(identifier);
    if (taskDefinitionOptional.isPresent()) {
      return ResponseEntity.ok(taskDefinitionOptional.get());
    } else {
      throw ServiceException.notFound("Task {0} not found.", identifier);
    }
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.TASK)
  @RequestMapping(
      value = "/tasks/{identifier}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> updateTask(@PathVariable("identifier") final String identifier, @RequestBody final TaskDefinition taskDefinition) {
    if (this.taskService.taskDefinitionExists(identifier)) {
      this.taskService.updateTaskDefinition(identifier, taskDefinition);
    } else {
      throw ServiceException.notFound("Task {0} not found.", identifier);
    }
    return ResponseEntity.accepted().build();
  }

//  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CUSTOMER)
  @RequestMapping(
      value = "/customers/{identifier}/actions",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<ProcessStep>> fetchProcessSteps(@PathVariable(value = "identifier") final String customerIdentifier) {
    this.throwIfCustomerNotExists(customerIdentifier);
    return ResponseEntity.ok(this.customerService.getProcessSteps(customerIdentifier));
  }

  private Pageable createPageRequest(final Integer pageIndex, final Integer size, final String sortColumn, final String sortDirection) {
    final Integer pageIndexToUse = pageIndex != null ? pageIndex : 0;
    final Integer sizeToUse = size != null ? size : 20;
    final String sortColumnToUse = sortColumn != null ? sortColumn : "identifier";
    final Sort.Direction direction = sortDirection != null ? Sort.Direction.valueOf(sortDirection.toUpperCase()) : Sort.Direction.ASC;
    return PageRequest.of(pageIndexToUse, sizeToUse, direction, sortColumnToUse);
  }

  private void throwIfCustomerNotExists(final String identifier) {
    if (!this.customerService.customerExists(identifier)) {
      throw ServiceException.notFound("Customer {0} not found.", identifier);
    }
  }

  private void throwIfIdentificationCardNotExists(final String number) {
    if (!this.customerService.identificationCardExists(number)) {
      throw ServiceException.notFound("Identification card {0} not found.", number);
    }
  }

  private void throwIfIdentificationCardScanNotExists(final String number, final String identifier) {
    if (!this.customerService.identificationCardScanExists(number, identifier)) {
      throw ServiceException.notFound("Identification card scan {0} not found.", identifier);
    }
  }

  private void throwIfInvalidSize(final Long size) {
    final Long maxSize = this.environment.getProperty("upload.image.max-size", Long.class);

    if(size > maxSize) {
      throw ServiceException.badRequest("Image can''t exceed size of {0}", maxSize);
    }
  }

  private void throwIfInvalidContentType(final String contentType) {
    if(!contentType.contains(MediaType.IMAGE_JPEG_VALUE)
            && !contentType.contains(MediaType.IMAGE_PNG_VALUE)) {
      throw ServiceException.badRequest("Only content type {0} and {1} allowed", MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);
    }
  }
}
