package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestAdminRole;
import static gov.ca.cwds.service.messages.MessageCode.ADMIN_CANNOT_UPDATE_HIMSELF;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.service.MappingService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.role.implementor.AbstractAdminActionsAuthorizer;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("authorizationService")
@Profile("idm")
public class AuthorizationServiceImpl implements AuthorizationService {

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

  @Override
  public void checkCanUpdateUser(String userId) {
    if (userId.equals(getCurrentUserName())) {
      throw exceptionFactory.createAuthorizationException(ADMIN_CANNOT_UPDATE_HIMSELF);
    }
    User user = getUserById(userId);
    checkCanUpdateUser(user);
  }

  @Override
  public boolean canUpdateUser(String userId) {
    return canAuthorizeOperation(userId, this::checkCanUpdateUser);
  }

  @Override
  public void checkCanUpdateUser(UserType existingUser) {
    if (existingUser.getUsername().equals(getCurrentUserName())) {
      throw exceptionFactory.createAuthorizationException(ADMIN_CANNOT_UPDATE_HIMSELF);
    }
    User user = composeUser(existingUser);
    checkCanUpdateUser(user);
  }

  void checkCanUpdateUser(User user) {
    getAdminActionsAuthorizer(user).checkCanUpdateUser();
  }

  private AdminActionsAuthorizer getAdminActionsAuthorizer(User user) {
    AbstractAdminActionsAuthorizer authorizer = adminRoleImplementorFactory.getAdminActionsAuthorizer(user);
    authorizer.setExceptionFactory(exceptionFactory);
    return authorizer;
  }

  @Override
  public boolean canEditRoles(User user) {
    return canAuthorizeOperation(user, this::checkCanEditRoles);
  }

  private <T> boolean canAuthorizeOperation(T input, Consumer<T> check) {
    try {
      check.accept(input);
    } catch (AdminAuthorizationException e) {
      return false;
    }
    return true;
  }

  @Override
  public void checkCanEditRoles(UserType cognitoUser) {
    User user = composeUser(cognitoUser);
    checkCanEditRoles(user);
  }

  private void checkCanEditRoles(User user) {
    checkCanUpdateUser(user);
    getAdminActionsAuthorizer(user).checkCanEditRoles();
  }

  @Override
  public void checkCanResendInvitationMessage(String userId) {
    User user = getUserById(userId);
    getAdminActionsAuthorizer(user).checkCanResendInvitationMessage();
  }

  private User getUserById(String userId) {
    UserType existingCognitoUser = cognitoServiceFacade.getCognitoUserById(userId);
    return composeUser(existingCognitoUser);
  }

  private User composeUser(UserType cognitoUser) {
    User user;
    if (OFFICE_ADMIN.equals(getStrongestAdminRole(getCurrentUser()))) {
      user = mappingService.toUser(cognitoUser);
    } else {
      user = mappingService.toUserWithoutDbData(cognitoUser);
    }
    return user;
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
