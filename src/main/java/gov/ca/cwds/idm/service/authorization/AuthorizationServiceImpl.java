package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestAdminRole;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.composeToGetFirstPageByEmail;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.MappingService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    UserType existingCognitoUser = cognitoServiceFacade.getCognitoUserById(userId);
    User user = composeUser(existingCognitoUser);
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
  public boolean canResendInvitationMessage(String email) {
    User user = getUserByEmail(email);
    return adminRoleImplementorFactory.getAdminActionsAuthorizer(user).canResendInvitationMessage();
  }

  private User getUserByEmail(String email) {
    List<UserType> cognitoUsers =
        cognitoServiceFacade.searchPage(composeToGetFirstPageByEmail(email)).getUsers();
    if (!CollectionUtils.isEmpty(cognitoUsers)) {
      return composeUser(cognitoUsers.get(0));
    } else {
      throw new IllegalStateException();
    }
  }

  private User composeUser(UserType cognitoUser) {
    User user;
    if (OFFICE_ADMIN.equals(getStrongestAdminRole(getCurrentUser()))) {
      user = mappingService.toUser(cognitoUser);
    } else {
      user = mappingService.toUserWithoutCwsData(cognitoUser);
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
