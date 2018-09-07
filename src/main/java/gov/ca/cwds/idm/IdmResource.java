package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.service.cognito.StandardUserAttribute.RACFID_STANDARD;
import static gov.ca.cwds.service.messages.MessageCode.IDM_USER_VALIDATION_FAILED;
import static gov.ca.cwds.service.messages.MessageCode.INVALID_DATE_FORMAT;
import static gov.ca.cwds.service.messages.MessageCode.USER_WITH_EMAIL_EXISTS_IN_IDM;
import static java.util.stream.Collectors.toList;

import gov.ca.cwds.idm.dto.IdmApiCustomError;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserAndOperation;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.dto.UserVerificationResult;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.idm.dto.UsersSearchCriteria;
import gov.ca.cwds.idm.persistence.model.Permission;
import gov.ca.cwds.idm.service.DictionaryProvider;
import gov.ca.cwds.idm.service.IdmService;
import gov.ca.cwds.rest.api.domain.PartialSuccessException;
import gov.ca.cwds.rest.api.domain.UserAlreadyExistsException;
import gov.ca.cwds.rest.api.domain.UserIdmValidationException;
import gov.ca.cwds.rest.api.domain.UserNotFoundPerryException;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@Profile("idm")
@RequestMapping(value = "/idm")
@SuppressWarnings({"squid:S1166"})
public class IdmResource {

  public static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd-HH.mm.ss.SSS";

  private static final Logger LOGGER = LoggerFactory.getLogger(IdmResource.class);

  @Autowired private IdmService idmService;

  @Autowired private DictionaryProvider dictionaryProvider;

  @Autowired private MessagesService messages;

  @GetMapping(value = "/users", produces = "application/json")
  @ApiOperation(
    value = "Users page",
    response = UsersPage.class,
    notes =
        "This service is used by batch job to build the ES index. The client of this service should have 'IDM-job' role."
            + "Once there is more items than a default page size (60) in the datasource you will get a paginationToken "
            + "in a responce. Use it as a parameter to get a next page."
  )
  @ApiResponses(value = {@ApiResponse(code = 401, message = "Not Authorized")})
  @PreAuthorize("hasAuthority(T(gov.ca.cwds.config.api.idm.Roles).IDM_JOB)")
  public UsersPage getUsers(
      @ApiParam(name = "paginationToken", value = "paginationToken for the next page")
          @RequestParam(name = "paginationToken", required = false)
          String paginationToken) {
    return idmService.getUserPage(paginationToken);
  }

  @PostMapping(
    value = "/users/search",
    consumes = "application/json",
    produces = "application/json"
  )
  @ApiResponses(value = {@ApiResponse(code = 401, message = "Not Authorized")})
  @ApiOperation(
    value = "Search users with given RACFIDs list",
    response = User.class,
    responseContainer = "List",
    notes = "This service is used by batch job to build the ES index. The client of this service should have 'IDM-job' role."
  )
  @PreAuthorize("hasAuthority(T(gov.ca.cwds.config.api.idm.Roles).IDM_JOB)")
  public List<User> searchUsersByRacfid(
      @ApiParam(required = true, name = "RACFIDs", value = "List of RACFIDs") @NotNull @RequestBody
          Set<String> racfids) {
    return idmService.searchUsers(new UsersSearchCriteria(RACFID_STANDARD, racfids));
  }

  @GetMapping(
      value = "/users/failed-operations",
      produces = "application/json"
  )
  @ApiResponses(value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 400, message = "Bad Request")
  })
  @ApiOperation(
      value = "Get list of failed User creates and updates in Dora",
      response = UserAndOperation.class,
      responseContainer = "List",
      notes = "This service is used by batch job to build the ES index. The client of this service should have 'IDM-job' role."
  )
  @PreAuthorize("hasAuthority(T(gov.ca.cwds.config.api.idm.Roles).IDM_JOB)")
  public ResponseEntity getFailedOperations(
      @ApiParam(required = true, name = "date",
          value = "Last date of successful batch job execution in yyyy-MM-dd-HH.mm.ss.SSS format")
      @RequestParam(name = "date")
          String lastJobDateStr) {
    LocalDateTime lastJobTime;
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN);
      lastJobTime = LocalDateTime.parse(lastJobDateStr, formatter);
    } catch (DateTimeParseException e) {
      String msg = messages.get(INVALID_DATE_FORMAT, DATETIME_FORMAT_PATTERN);
      LOGGER.error(msg, e);
      return createCustomResponseEntity(HttpStatus.BAD_REQUEST, INVALID_DATE_FORMAT, msg);
    }
    return ResponseEntity.ok().body(idmService.getFailedOperations(lastJobTime));
  }

  @GetMapping(value = "/users/{id}", produces = "application/json")
  @ApiOperation(value = "Find User by ID", response = User.class)
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @PreAuthorize("hasAnyAuthority("
      + "T(gov.ca.cwds.config.api.idm.Roles).CWS_ADMIN, "
      + "T(gov.ca.cwds.config.api.idm.Roles).COUNTY_ADMIN)")
  public ResponseEntity<User> getUser(
      @ApiParam(required = true, value = "The unique user ID", example = "userId1")
          @PathVariable
          @NotNull
          String id) {

    try {
      User user = idmService.findUser(id);
      return ResponseEntity.ok().body(user);
    } catch (UserNotFoundPerryException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PatchMapping(
    value = "/users/{id}",
    consumes = "application/json"
  )
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(
    value = {
      @ApiResponse(code = 204, message = "No Content"),
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Update User")
  @PreAuthorize("hasAnyAuthority("
      + "T(gov.ca.cwds.config.api.idm.Roles).CWS_ADMIN, "
      + "T(gov.ca.cwds.config.api.idm.Roles).COUNTY_ADMIN)")
  public ResponseEntity updateUser(
      @ApiParam(required = true, value = "The unique user ID", example = "userId1")
          @PathVariable
          @NotNull
          String id,
      @ApiParam(required = true, name = "userUpdateData", value = "The User update data")
          @NotNull
          @RequestBody
          UserUpdate updateUserDto) {
    try {
      idmService.updateUser(id, updateUserDto);
      return ResponseEntity.noContent().build();
    } catch (UserNotFoundPerryException e) {
      return ResponseEntity.notFound().build();
    } catch (PartialSuccessException e) {
      return createCustomResponseEntity(
          HttpStatus.INTERNAL_SERVER_ERROR,
          e.getErrorCode(),
          e.getMessage(),
          e.getCauses().stream().map(Exception::getMessage).collect(toList()));
    }
  }

  @PostMapping(value = "/users", consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(
      value = {
        @ApiResponse(code = 201, message = "New User is created successfully"),
        @ApiResponse(code = 400, message = "Bad Request. Provided JSON has invalid data"),
        @ApiResponse(
            code = 401,
            message =
                "Not Authorized. For example county_name differs from the county admin belongs to."),
        @ApiResponse(code = 409, message = "Conflict. User with the same email already exists")
      })
  @ApiOperation(
      value = "Create new User",
      notes =
          "Only the following properties of the input User JSON will be used at new User creation:\n "
              + "email, first_name, last_name, county_name, RACFID, permissions, office, phone_number.\n "
              + "Other properties values will be set by the system automatically.\n"
              + "Required properties are: email, first_name, last_name, county_name.")
  @PreAuthorize("hasAnyAuthority("
      + "T(gov.ca.cwds.config.api.idm.Roles).CWS_ADMIN, "
      + "T(gov.ca.cwds.config.api.idm.Roles).COUNTY_ADMIN)")
  public ResponseEntity createUser(
      @ApiParam(required = true, name = "User", value = "The User create data")
          @NotNull
          @Valid
          @RequestBody
          User user) {
    try {
      String newUserId = idmService.createUser(user);
      URI locationUri = getNewUserLocationUri(newUserId);
      return ResponseEntity.created(locationUri).build();

    } catch (UserAlreadyExistsException e) {
      return createCustomResponseEntity(
          HttpStatus.CONFLICT, USER_WITH_EMAIL_EXISTS_IN_IDM, e.getMessage());

    } catch (UserIdmValidationException e) {
      return createCustomResponseEntity(
          HttpStatus.BAD_REQUEST,
          IDM_USER_VALIDATION_FAILED,
          e.getMessage(),
          Collections.singletonList(e.getCause().getMessage()));

    } catch (PartialSuccessException e) {
      URI locationUri = getNewUserLocationUri(e.getUserId());
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(locationUri);

      return createCustomResponseEntity(
          HttpStatus.INTERNAL_SERVER_ERROR,
          e.getErrorCode(),
          e.getMessage(),
          headers,
          e.getCauses().stream().map(Exception::getMessage).collect(toList()));
    }
  }

  private URI getNewUserLocationUri(String newUserId){
    return  ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(newUserId)
        .toUri();
  }

  @GetMapping(value = "/permissions", produces = "application/json")
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(
    value = "Get List of possible permissions",
    response = Permission.class,
    responseContainer = "List"
  )
  @PreAuthorize("hasAnyAuthority("
      + "T(gov.ca.cwds.config.api.idm.Roles).CWS_ADMIN, "
      + "T(gov.ca.cwds.config.api.idm.Roles).COUNTY_ADMIN)")
  public List<Permission> getPermissions() {
    return  dictionaryProvider.getPermissions();
  }

  @GetMapping(value = "users/verify", produces = "application/json")
  @ApiOperation(value = "Check if user can be created by racfId and email", response = UserVerificationResult.class)
  @ApiResponses(value = {@ApiResponse(code = 401, message = "Not Authorized")})
  @PreAuthorize("hasAnyAuthority("
      + "T(gov.ca.cwds.config.api.idm.Roles).CWS_ADMIN, "
      + "T(gov.ca.cwds.config.api.idm.Roles).COUNTY_ADMIN)")
  public ResponseEntity<UserVerificationResult> verifyUser(
      @ApiParam(required = true, name = "racfid", value = "The RACFID to verify user by in CWS/CMS")
          @NotNull
          @RequestParam("racfid")
          String racfId,
      @ApiParam(required = true, name = "email", value = "The email to verify user by in Cognito")
          @NotNull
          @RequestParam("email")
          String email) {
    return ResponseEntity.ok().body(idmService.verifyUser(racfId, email));
  }

  private static ResponseEntity<IdmApiCustomError> createCustomResponseEntity(
      HttpStatus httpStatus, MessageCode errorCode, String msg) {
    return new ResponseEntity<>(
        new IdmApiCustomError(httpStatus, errorCode, msg), httpStatus);
  }

  private static ResponseEntity<IdmApiCustomError> createCustomResponseEntity(
      HttpStatus httpStatus, MessageCode errorCode, String msg, List<String> causes) {
    return new ResponseEntity<>(
        new IdmApiCustomError(httpStatus, errorCode, msg, causes), httpStatus);
  }

  private static ResponseEntity<IdmApiCustomError> createCustomResponseEntity(
      HttpStatus httpStatus, MessageCode errorCode, String msg, MultiValueMap<String, String> headers, List<String> causes) {
    return new ResponseEntity<>(
        new IdmApiCustomError(httpStatus, errorCode, msg, causes), headers, httpStatus);
  }
}