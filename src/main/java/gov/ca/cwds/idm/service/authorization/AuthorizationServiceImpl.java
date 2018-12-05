package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.service.messages.MessageCode.ADMIN_CANNOT_UPDATE_HIMSELF;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.service.MappingService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.role.implementor.AbstractAdminActionsAuthorizer;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("authorizationService")
@Profile("idm")
public class AuthorizationServiceImpl implements AuthorizationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

  private static final String ROLES_EDITING = "roles editing";
  private static final String PERMISSIONS_EDITING = "permissions editing";
  private static final String USER_UPDATE = "user update";

  private CognitoServiceFacade cognitoServiceFacade;

  private MappingService mappingService;

  private AdminRoleImplementorFactory adminRoleImplementorFactory;

  private ExceptionFactory exceptionFactory;

  @Override
  public void checkCanViewUser(User user) {
    getAdminActionsAuthorizer(user).checkCanViewUser();
  }

  @Override
  public void checkCanCreateUser(User user) {
    getAdminActionsAuthorizer(user).checkCanCreateUser();
  }

  void checkCanUpdateUser(String userId) {
    if (userId.equals(getCurrentUserName())) {
      throw exceptionFactory.createAuthorizationException(ADMIN_CANNOT_UPDATE_HIMSELF);
    }
    User user = getUserById(userId);
    checkCanUpdateUser(user);
  }

  @Override
  public boolean canUpdateUser(String userId) {
    return canAuthorizeOperation(userId, this::checkCanUpdateUser, USER_UPDATE);
  }

  @Override
  public void checkCanUpdateUser(UserType existingUser, UserUpdate updateUserDto) {
    if (existingUser.getUsername().equals(getCurrentUserName())) {
      throw exceptionFactory.createAuthorizationException(ADMIN_CANNOT_UPDATE_HIMSELF);
    }
    User user = mappingService.toUser(existingUser);
    checkCanUpdateUser(user);
    authorizeRolesUpdate(existingUser, updateUserDto);
    authorizePermissionsUpdate(existingUser, updateUserDto);
  }

  private void authorizeRolesUpdate(UserType existedCognitoUser, UserUpdate updateUserDto) {
    if (updateUserDto.getRoles() == null) {
      return;
    }
    if (wasRolesActuallyEdited(existedCognitoUser, updateUserDto)) {
      checkCanEditRoles(existedCognitoUser);
    }
  }

  private void authorizePermissionsUpdate(UserType existedCognitoUser, UserUpdate updateUserDto) {
    if (updateUserDto.getPermissions() == null) {
      return;
    }
    if (wasPermissionsActuallyEdited(existedCognitoUser, updateUserDto)) {
      checkCanEditPermissions(existedCognitoUser);
    }
  }

  private boolean wasRolesActuallyEdited(UserType existedCognitoUser, UserUpdate updateUserDto) {
    return wasActuallyEdited(
        CognitoUtils.getRoles(existedCognitoUser),
        updateUserDto.getRoles());
  }

  private boolean wasPermissionsActuallyEdited(UserType existedCognitoUser, UserUpdate updateUserDto) {
    return wasActuallyEdited(
        CognitoUtils.getPermissions(existedCognitoUser),
        updateUserDto.getPermissions());
  }

    private boolean wasActuallyEdited(Set<String> oldSet, Set<String> newSet) {
    return !CollectionUtils.isEqualCollection(oldSet, newSet);
  }

  void checkCanUpdateUser(User user) {
    getAdminActionsAuthorizer(user).checkCanUpdateUser();
  }

  private void checkCanEditRoles(UserType cognitoUser) {
    User user = mappingService.toUser(cognitoUser);
    checkCanEditRoles(user);
  }

  private void checkCanEditRoles(User user) {
    getAdminActionsAuthorizer(user).checkCanEditRoles();
  }

  @Override
  public boolean canEditRoles(User user) {
    return canAuthorizeOperation(user, this::checkCanEditRoles, ROLES_EDITING);
  }

  private void checkCanEditPermissions(UserType cognitoUser) {
    User user = mappingService.toUser(cognitoUser);
    checkCanUpdateUser(user);
  }

  private void checkCanEditPermissions(User user) {
    checkCanUpdateUser(user);
    getAdminActionsAuthorizer(user).checkCanEditPermissions();
  }

  @Override
  public boolean canEditPermissions(User  user) {
    return canAuthorizeOperation(user, this::checkCanEditPermissions, PERMISSIONS_EDITING);
  }

  private AdminActionsAuthorizer getAdminActionsAuthorizer(User user) {
    AbstractAdminActionsAuthorizer authorizer = adminRoleImplementorFactory.getAdminActionsAuthorizer(user);
    authorizer.setExceptionFactory(exceptionFactory);
    return authorizer;
  }

  @SuppressWarnings({"squid:S1166", "fb-contrib:EXS_EXCEPTION_SOFTENING_RETURN_FALSE"})
  //squid:S1166: exceptions stack trace can be omitted in this context
  //fb-contrib:EXS_EXCEPTION_SOFTENING_RETURN_FALSE: our design needs a boolean result
  private <T> boolean canAuthorizeOperation(T input, Consumer<T> check, String operationName) {
    try {
      check.accept(input);
    } catch (AdminAuthorizationException e) {
      LOGGER.info("{} can not be authorized, since: {}", operationName, e.getUserMessage());
      return false;
    }
    return true;
  }

  @Override
  public void checkCanResendInvitationMessage(String userId) {
    User user = getUserById(userId);
    getAdminActionsAuthorizer(user).checkCanResendInvitationMessage();
  }

  private User getUserById(String userId) {
    UserType existingCognitoUser = cognitoServiceFacade.getCognitoUserById(userId);
    return mappingService.toUser(existingCognitoUser);
  }

  @Autowired
  public void setCognitoServiceFacade(CognitoServiceFacade cognitoServiceFacade) {
    this.cognitoServiceFacade = cognitoServiceFacade;
  }

  @Autowired
  public void setMappingService(MappingService mappingService) {
    this.mappingService = mappingService;
  }

  @Autowired
  public void setAdminRoleImplementorFactory(
      AdminRoleImplementorFactory adminRoleImplementorFactory) {
    this.adminRoleImplementorFactory = adminRoleImplementorFactory;
  }

  @Autowired
  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
