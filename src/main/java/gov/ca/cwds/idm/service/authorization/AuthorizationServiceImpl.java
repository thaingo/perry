package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestAdminRole;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.MappingService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("authorizationService")
@Profile("idm")
public class AuthorizationServiceImpl implements AuthorizationService {

  private CognitoServiceFacade cognitoServiceFacade;

  private MappingService mappingService;

  private AdminRoleImplementorFactory adminRoleImplementorFactory;

  @Override
  public boolean canViewUser(User user) {
    return adminRoleImplementorFactory.getAdminActionsAuthorizer(user).canViewUser();
  }

  @Override
  public boolean canCreateUser(User user) {
    return adminRoleImplementorFactory.getAdminActionsAuthorizer(user).canCreateUser();
  }

  @Override
  public boolean canUpdateUser(String userId) {
    // admin can't update himself
    if (userId.equals(getCurrentUserName())) {
      return false;
    }
    User user = getUserById(userId);
    return canUpdateUser(user);
  }

  @Override
  public boolean canUpdateUser(UserType existingUser) {
    // admin can't update himself
    if (existingUser.getUsername().equals(getCurrentUserName())) {
      return false;
    }
    User user = composeUser(existingUser);
    return canUpdateUser(user);
  }

  boolean canUpdateUser(User user) {
    return adminRoleImplementorFactory.getAdminActionsAuthorizer(user).canUpdateUser();
  }

  @Override
  public boolean canEditRoles(User user) {
    return canUpdateUser(user) &&
        adminRoleImplementorFactory.getAdminActionsAuthorizer(user).canEditRoles();
  }

  @Override
  public boolean canEditRoles(UserType cognitoUser) {
    User user = composeUser(cognitoUser);
    return canEditRoles(user);
  }

  @Override
  public boolean canResendInvitationMessage(String userId) {
    User user = getUserById(userId);
    return adminRoleImplementorFactory.getAdminActionsAuthorizer(user).canResendInvitationMessage();
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
}
