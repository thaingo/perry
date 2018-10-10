package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestAdminRole;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.MappingService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.role.implementor.RoleImplementorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("authorizationService")
@Profile("idm")
public class AuthorizationServiceImpl implements AuthorizationService {

  private CognitoServiceFacade cognitoServiceFacade;

  private MappingService mappingService;

  private RoleImplementorFactory roleImplementorFactory;

  @Override
  public boolean canViewUser(User user) {
    return roleImplementorFactory.getAdminActionsAuthorizer(user).canViewUser();
  }

  @Override
  public boolean canCreateUser(User user) {
    return roleImplementorFactory.getAdminActionsAuthorizer(user).canCreateUser();
  }

  @Override
  public boolean canUpdateUser(String userId) {
    //admin can't update himself
    if (userId.equals(getCurrentUserName())) {
      return false;
    }
    User user = getUserFromUserId(userId);
    return roleImplementorFactory.getAdminActionsAuthorizer(user).canUpdateUser();
  }

  @Override
  public boolean canResendInvitationMessage(String userId) {
    User user = getUserFromUserId(userId);
    return roleImplementorFactory.getAdminActionsAuthorizer(user).canResendInvitationMessage();
  }

  private User getUserFromUserId(String userId) {
    UserType cognitoUser = cognitoServiceFacade.getCognitoUserById(userId);

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
  public void setRoleImplementorFactory(
      RoleImplementorFactory roleImplementorFactory) {
    this.roleImplementorFactory = roleImplementorFactory;
  }

}
