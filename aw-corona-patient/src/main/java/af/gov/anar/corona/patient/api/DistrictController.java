package af.gov.anar.corona.patient.api;

import af.gov.anar.api.annotation.ThrowsException;
import af.gov.anar.api.annotation.ThrowsExceptions;
import af.gov.anar.api.handler.ResponseHandler;
import af.gov.anar.api.handler.exception.InternalServerProblemException;
import af.gov.anar.api.handler.exception.ResourceNotFoundException;
import af.gov.anar.corona.patient.service.DistrictService;
import af.gov.anar.corona.patient.model.District;
import af.gov.anar.corona.infrastructure.exception.UnAuthorizedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/administration/districts")
@Api(value="district-api", description="API for District Entity", tags = {"district"}, protocols = "http")
public class DistrictController extends ResponseHandler {

    @Autowired
    private DistrictService service;

    @ApiOperation(value = "get all entity instances", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @ThrowsExceptions(value = {
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception= ResourceNotFoundException.class),
            @ThrowsException(status = HttpStatus.UNAUTHORIZED, exception = UnAuthorizedException.class),
            @ThrowsException(status = HttpStatus.INTERNAL_SERVER_ERROR, exception = InternalServerProblemException.class)
    })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Iterable<District>> findAll()
    {
        return ResponseEntity.ok(service.findAll());
    }

    @ApiOperation(value = "get all not-deleted entity instances", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @ThrowsExceptions(value = {
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception= ResourceNotFoundException.class),
            @ThrowsException(status = HttpStatus.UNAUTHORIZED, exception = UnAuthorizedException.class),
            @ThrowsException(status = HttpStatus.INTERNAL_SERVER_ERROR, exception = InternalServerProblemException.class)
    })
    @GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Iterable<District>> findAllThatIsNotDeleted()
    {
        return ResponseEntity.ok(service.findAllThatIsNotDeleted());
    }

    @ApiOperation(value = "get all deleted entity instances", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @ThrowsExceptions(value = {
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception= ResourceNotFoundException.class),
            @ThrowsException(status = HttpStatus.UNAUTHORIZED, exception = UnAuthorizedException.class),
            @ThrowsException(status = HttpStatus.INTERNAL_SERVER_ERROR, exception = InternalServerProblemException.class)
    })
    @GetMapping(value = "/deleted", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Iterable<District>> findAllThatIsDeleted()
    {
        return ResponseEntity.ok(service.findAllThatIsDeleted());
    }

    @ApiOperation(value = "find entity instance by id", response = District.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @ThrowsExceptions(value = {
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception= ResourceNotFoundException.class),
            @ThrowsException(status = HttpStatus.UNAUTHORIZED, exception = UnAuthorizedException.class),
            @ThrowsException(status = HttpStatus.INTERNAL_SERVER_ERROR, exception = InternalServerProblemException.class)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Optional<District>> findById(@PathVariable(name="id", required = true) Long id)
    {
        return ResponseEntity.ok(service.findById(id));
    }

    @ApiOperation(value = "add a new entry instance ", response = District.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @ThrowsExceptions(value = {
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception= ResourceNotFoundException.class),
            @ThrowsException(status = HttpStatus.UNAUTHORIZED, exception = UnAuthorizedException.class),
            @ThrowsException(status = HttpStatus.INTERNAL_SERVER_ERROR, exception = InternalServerProblemException.class)
    })
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<District> save (@RequestBody(required = true) District obj)
    {
        return  ResponseEntity.ok(service.save(obj));
    }

    @ApiOperation(value = "update existing instance of entity ", response = District.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @ThrowsExceptions(value = {
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception= ResourceNotFoundException.class),
            @ThrowsException(status = HttpStatus.UNAUTHORIZED, exception = UnAuthorizedException.class),
            @ThrowsException(status = HttpStatus.INTERNAL_SERVER_ERROR, exception = InternalServerProblemException.class)
    })
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<District> update (@RequestBody(required = true) District obj, @PathVariable(name = "id", required = true) Long id)
    {
        obj.setId(id);
        return ResponseEntity.ok(service.save(obj));
    }

    @ApiOperation(value = "Deleted an instance of entity", response = District.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @ThrowsExceptions(value = {
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception= ResourceNotFoundException.class),
            @ThrowsException(status = HttpStatus.UNAUTHORIZED, exception = UnAuthorizedException.class),
            @ThrowsException(status = HttpStatus.INTERNAL_SERVER_ERROR, exception = InternalServerProblemException.class)
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Void> delete(@PathVariable(name = "id", required = true) Long id)
    {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
