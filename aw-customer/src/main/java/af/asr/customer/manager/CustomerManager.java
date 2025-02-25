
package af.asr.customer.manager;

import af.asr.customer.domain.*;
import af.asr.customer.exception.*;
import af.gov.anar.api.annotation.ThrowsException;
import af.gov.anar.api.annotation.ThrowsExceptions;
import af.gov.anar.lang.validation.constraints.ValidIdentifier;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;

@SuppressWarnings("unused")
@FeignClient(name="customer-v1", path="/customer/v1", configuration= FeignAutoConfiguration.class)
public interface CustomerManager {

  @RequestMapping(
      value = "/customers",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.CONFLICT, exception = CustomerAlreadyExistsException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = CustomerValidationException.class)
  })
  void createCustomer(@RequestBody final Customer customer);

  @RequestMapping(
      value = "/customers",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  CustomerPage fetchCustomers(@RequestParam(value = "term", required = false) final String term,
                              @RequestParam(value = "includeClosed", required = false) final Boolean includeClosed,
                              @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                              @RequestParam(value = "size", required = false) final Integer size,
                              @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                              @RequestParam(value = "sortDirection", required = false) final String sortDirection);

  @RequestMapping(
      value = "/customers/{identifier}",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class)
  Customer findCustomer(@PathVariable("identifier") final String identifier);

  default boolean isCustomerInGoodStanding(final String customerIdentifier) {
    final Customer customer;
    try {
      customer = this.findCustomer(customerIdentifier);
    }
    catch (CustomerNotFoundException e) {
      return false;
    }
    final Customer.State state = Customer.State.valueOf(customer.getCurrentState());
    return (state == Customer.State.ACTIVE);
  }

  @RequestMapping(
      value = "/customers/{identifier}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = CustomerValidationException.class)
  })
  void updateCustomer(@PathVariable("identifier") final String identifier, @RequestBody final Customer customer);

  @RequestMapping(
      value = "/customers/{identifier}/commands",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = CommandExecutionException.class)
  })
  void customerCommand(@PathVariable("identifier") final String identifier, @RequestBody final Command command);

  @RequestMapping(
      value = "/customers/{identifier}/commands",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class)
  List<Command> fetchCustomerCommands(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/customers/{identifier}/tasks/{taskIdentifier}",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class),
      @ThrowsException(status = HttpStatus.CONFLICT, exception = TaskAlreadyExistsException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = TaskValidationException.class)
  })
  void addTaskToCustomer(@PathVariable("identifier") final String identifier,
                         @PathVariable("taskIdentifier") final String taskIdentifier);

  @RequestMapping(
      value = "/customers/{identifier}/tasks/{taskIdentifier}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class),
      @ThrowsException(status = HttpStatus.CONFLICT, exception = TaskExecutionException.class)
  })
  void taskForCustomerExecuted(@PathVariable("identifier") final String identifier,
                               @PathVariable("taskIdentifier") final String taskIdentifier);

  @RequestMapping(
      value = "/customers/{identifier}/tasks",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = TaskNotFoundException.class)
  List<TaskDefinition> findTasksForCustomer(@PathVariable("identifier") final String identifier,
                                            @RequestParam(value = "includeExecuted", required = false) final Boolean includeExecuted);

  @RequestMapping(
      value = "/customers/{identifier}/address",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = AddressValidationException.class)
  })
  void putAddress(@PathVariable("identifier") final String identifier, @RequestBody final Address address);

  @RequestMapping(
      value = "/customers/{identifier}/contact",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = ContactDetailValidationException.class)
  })
  void putContactDetails(@PathVariable("identifier") final String identifier,
                         @RequestBody final List<ContactDetail> contactDetails);

  @RequestMapping(
          value = "/customers/{identifier}/identifications",
          method = RequestMethod.GET,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
          @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class)
  })
  List<IdentificationCard> fetchIdentificationCards(@PathVariable("identifier") final String identifier);

  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}",
          method = RequestMethod.GET,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = IdentificationCardNotFoundException.class)
  IdentificationCard findIdentificationCard(@PathVariable("identifier") final String identifier,
                                            @PathVariable("number") final String number);

  @RequestMapping(
          value = "/customers/{identifier}/identifications",
          method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
          @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class),
          @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = IdentificationCardValidationException.class)
  })
  void createIdentificationCard(@PathVariable("identifier") final String identifier,
                                @RequestBody final IdentificationCard identificationCard);

  @RequestMapping(
      value = "/customers/{identifier}/identifications/{number}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = IdentificationCardNotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = IdentificationCardValidationException.class)
  })
  void updateIdentificationCard(@PathVariable("identifier") final String identifier,
                                @PathVariable("number") final String number,
                                @RequestBody final IdentificationCard identificationCard);

  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}",
          method = RequestMethod.DELETE,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
          @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class)
  })
  void deleteIdentificationCard(@PathVariable("identifier") final String identifier,
                                @PathVariable("number") final String number);

  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}/scans",
          method = RequestMethod.GET,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
          @ThrowsException(status = HttpStatus.NOT_FOUND, exception = IdentificationCardNotFoundException.class)
  })
  List<IdentificationCardScan> fetchIdentificationCardScans(@PathVariable("identifier") final String identifier,
                                                            @PathVariable("number") final String number);

  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}/scans/{scanIdentifier}",
          method = RequestMethod.GET,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
          @ThrowsException(status = HttpStatus.NOT_FOUND, exception = ScanNotFoundException.class)
  })
  IdentificationCardScan findIdentificationCardScan(@PathVariable("identifier") final String identifier,
                                                     @PathVariable("number") final String number,
                                                     @PathVariable("scanIdentifier") final String scanIdentifier);

  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}/scans/{scanIdentifier}/image",
          method = RequestMethod.GET,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
          @ThrowsException(status = HttpStatus.NOT_FOUND, exception = ScanNotFoundException.class)
  })
  byte[] fetchIdentificationCardScanImage(@PathVariable("identifier") final String identifier,
                                          @PathVariable("number") final String number,
                                          @PathVariable("scanIdentifier") final String scanIdentifier);

  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}/scans",
          method = RequestMethod.POST,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  @ThrowsExceptions({
          @ThrowsException(status = HttpStatus.NOT_FOUND, exception = IdentificationCardNotFoundException.class),
          @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = ScanValidationException.class),
          @ThrowsException(status = HttpStatus.CONFLICT, exception = ScanAlreadyExistsException.class)
  })
  void postIdentificationCardScan(@PathVariable("identifier") final String identifier,
                                  @PathVariable("number") final String number,
                                  @RequestParam("scanIdentifier") @ValidIdentifier final String scanIdentifier,
                                  @RequestParam("description") @Size(max = 4096) final String description,
                                  @RequestBody final MultipartFile image);

  @RequestMapping(
          value = "/customers/{identifier}/identifications/{number}/scans/{scanIdentifier}",
          method = RequestMethod.DELETE,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  void deleteScan(@PathVariable("identifier") final String identifier,
                  @PathVariable("number") final String number,
                  @PathVariable("scanIdentifier") final String scanIdentifier);

  @RequestMapping(
          value = "/customers/{identifier}/portrait",
          method = RequestMethod.GET,
          produces = MediaType.ALL_VALUE
  )
  @ThrowsExceptions({
          @ThrowsException(status = HttpStatus.NOT_FOUND, exception = PortraitNotFoundException.class),
  })
  byte[] getPortrait(@PathVariable("identifier") final String identifier);

  @RequestMapping(
          value = "/customers/{identifier}/portrait",
          method = RequestMethod.POST,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  @ThrowsExceptions({
          @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class),
          @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = DocumentValidationException.class),
  })
  void postPortrait(@PathVariable("identifier") final String identifier,
                   @RequestBody final MultipartFile portrait);

  @RequestMapping(
          value = "/customers/{identifier}/portrait",
          method = RequestMethod.DELETE,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  void deletePortrait(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/tasks",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.CONFLICT, exception = TaskAlreadyExistsException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = TaskValidationException.class)
  })
  void createTask(@RequestBody final TaskDefinition taskDefinition);

  @RequestMapping(
      value = "/tasks",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  List<TaskDefinition> fetchAllTasks();

  @RequestMapping(
      value = "/tasks/{identifier}",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = TaskNotFoundException.class)
  TaskDefinition findTask(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/tasks/{identifier}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = TaskNotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = TaskValidationException.class)
  })
  void updateTask(@PathVariable("identifier") final String identifier, @RequestBody final TaskDefinition taskDefinition);

  @RequestMapping(
      value = "/customers/{identifier}/actions",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CustomerNotFoundException.class)
  List<ProcessStep> fetchProcessSteps(@PathVariable(value = "identifier") final String customerIdentifier);
}
