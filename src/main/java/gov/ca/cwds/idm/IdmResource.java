package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.service.cognito.StandardUserAttribute.RACFID_STANDARD;
import static gov.ca.cwds.util.Utils.URL_DATETIME_FORMATTER;

import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.idm.dto.RegistrationResubmitResponse;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserAndOperation;
import gov.ca.cwds.idm.dto.UserByIdResponse;
import gov.ca.cwds.idm.dto.UserEditDetails;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.dto.UserVerificationResult;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.idm.dto.UsersSearchCriteria;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.DictionaryProvider;
import gov.ca.cwds.idm.service.IdmService;
import gov.ca.cwds.idm.service.OfficeService;
import gov.ca.cwds.idm.service.authorization.AuthorizationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@Profile("idm")
@RequestMapping(value = "/idm")
@SuppressWarnings({"squid:S1166"})
public class IdmResource {



  @Autowired
  private IdmService idmService;

  @Autowired
  private DictionaryProvider dictionaryProvider;

  @Autowired
  private OfficeService officeService;

  @Autowired
  private AuthorizationService authorizationService;

  @RequestMapping(method = RequestMethod.GET, value = "/users", produces = "application/json")
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

  @RequestMapping(
      method = RequestMethod.POST,
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

  @RequestMapping(
      method = RequestMethod.GET,
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
    LocalDateTime lastJobTime = LocalDateTime.parse(lastJobDateStr, URL_DATETIME_FORMATTER);
    return ResponseEntity.ok().body(idmService.getFailedOperations(lastJobTime));
  }

  @RequestMapping(method = RequestMethod.GET, value = "/users/{id}", produces = "application/json")
  @ApiOperation(value = "Find User by ID", response = UserByIdResponse.class)
  @ApiResponses(
      value = {
          @ApiResponse(code = 401, message = "Not Authorized"),
          @ApiResponse(code = 404, message = "Not found")
      }
  )
  @PreAuthorize("@userRoleService.isAdmin(principal)")
  public ResponseEntity<UserByIdResponse> getUser(
      @ApiParam(required = true, value = "The unique user ID", example = "userId1")
      @PathVariable
      @NotNull
          String id) {

    User user = idmService.findUser(id);
    UserEditDetails editDetails = authorizationService.getEditDetails(user);
    UserByIdResponse response = new UserByIdResponse(user, editDetails);
    return ResponseEntity.ok().body(response);
  }

  @RequestMapping(
      method = RequestMethod.PATCH,
      value = "/users/{id}",
      consumes = "application/json"
  )
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(
      value = {
          @ApiResponse(code = 204, message = "No Content"),
          @ApiResponse(code = 401, message = "Not Authorized"),
          @ApiResponse(code = 404, message = "Not found"),
          @ApiResponse(code = 409, message = "Conflict. User with the same email already exists")
      }
  )
  @ApiOperation(value = "Update User")
  @PreAuthorize("@userRoleService.isAdmin(principal) &&  " +
      " !@userRoleService.isCalsAdminStrongestRole(principal)")
  public ResponseEntity updateUser(
      @ApiParam(required = true, value = "The unique user ID", example = "userId1")
      @PathVariable
      @NotNull
          String id,
      @ApiParam(required = true, name = "userUpdateData", value = "The User update data")
      @NotNull
      @RequestBody
          UserUpdate updateUserDto) {

    idmService.updateUser(id, updateUserDto);
    return ResponseEntity.noContent().build();
  }

  @RequestMapping(method = RequestMethod.POST, value = "/users", consumes = "application/json")
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
  @PreAuthorize("@userRoleService.isAdmin(principal) &&  " +
      " !@userRoleService.isCalsAdminStrongestRole(principal)")
  public ResponseEntity createUser(
      @ApiParam(required = true, name = "User", value = "The User create data")
      @NotNull
      @Valid
      @RequestBody
          User user) {

    String newUserId = idmService.createUser(user);
    URI locationUri = getNewUserLocationUri(newUserId);
    return ResponseEntity.created(locationUri).build();
  }

  static URI getNewUserLocationUri(String newUserId) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(newUserId)
        .toUri();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/permissions", produces = "application/json")
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
  @PreAuthorize("@userRoleService.isAdmin(principal)")
  public List<Permission> getPermissions() {
    return dictionaryProvider.getPermissions();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/roles", produces = "application/json")
  @ApiResponses(
      value = {
          @ApiResponse(code = 401, message = "Not Authorized"),
          @ApiResponse(code = 404, message = "Not found")
      }
  )
  @ApiOperation(
      value = "Get List of possible roles",
      responseContainer = "List"
  )
  @PreAuthorize("@userRoleService.isAdmin(principal)")
  public List<Map<String, String>> getRoles() {
    return dictionaryProvider.getRoles();
  }

  @RequestMapping(method = RequestMethod.GET, value = "users/verify", produces = "application/json")
  @ApiOperation(value = "Check if user can be created by racfId and email", response = UserVerificationResult.class)
  @ApiResponses(value = {@ApiResponse(code = 401, message = "Not Authorized")})
  @PreAuthorize("@userRoleService.isAdmin(principal) && "
      + "!@userRoleService.isCalsAdminStrongestRole(principal)")
  public ResponseEntity<UserVerificationResult> verifyIfUserCanBeCreated(
      @ApiParam(required = true, name = "racfid", value = "The RACFID to verify user by in CWS/CMS")
      @NotNull
      @RequestParam("racfid")
          String racfId,
      @ApiParam(required = true, name = "email", value = "The email to verify user by in Cognito")
      @NotNull
      @RequestParam("email")
          String email) {
    return ResponseEntity.ok().body(idmService.verifyIfUserCanBeCreated(racfId, email));
  }

  @RequestMapping(
      method = RequestMethod.POST,
      value = "users/{id}/registration-request",
      produces = "application/json")
  @ApiOperation(
      value =
          "Resend the invitation message to a user that already exists and reset the expiration\n "
              + "limit on the user's account by admin.",
      response = ResponseEntity.class)
  @ApiResponses(
      value = {
          @ApiResponse(code = 401, message = "Not Authorized"),
          @ApiResponse(code = 404, message = "Not found")
      }
  )
  @PreAuthorize("@userRoleService.isAdmin(principal) &&  " +
      " !@userRoleService.isCalsAdminStrongestRole(principal)")
  public ResponseEntity<RegistrationResubmitResponse> resendInvitationEmail(
      @ApiParam(required = true, value = "The unique user ID", example = "userId1")
      @NotNull
      @PathVariable("id")
          String id) {

    RegistrationResubmitResponse response = idmService.resendInvitationMessage(id);
    return ResponseEntity.ok().body(response);
  }

  @RequestMapping(
      method = RequestMethod.GET,
      value = "/admin-offices",
      produces = "application/json"
  )
  @ApiResponses(value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
  })
  @ApiOperation(
      value = "Get list of offices managed by the current admin",
      response = CwsOffice.class,
      responseContainer = "List"
  )
  @PreAuthorize("@userRoleService.isAdmin(principal) &&  " +
      " !@userRoleService.isCalsAdminStrongestRole(principal)")
  public ResponseEntity getAdminOffices() {
    return ResponseEntity.ok().body(officeService.getOfficesByAdmin());
  }
}