package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.MappingService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("authorizationService")
@Profile("idm")
public class AuthorizationServiceImpl implements AuthorizationService {

  private CognitoServiceFacade cognitoServiceFacade;

  private MappingService mappingService;

  @Override
  public boolean canViewUser(User user) {
    return createAdminActionsAuthorizer(user).canViewUser();
  }

  @Override
  public boolean canCreateUser(User user) {
    return createAdminActionsAuthorizer(user).canCreateUser();
  }

  @Override
  public boolean canUpdateUser(String userId) {
    //admin can't update himself
    if (userId.equals(getCurrentUserName())) {
      return false;
    }
    User user = getUserFromUserId(userId);
    return createAdminActionsAuthorizer(user).canUpdateUser();
  }

  @Override
  public boolean canResendInvitationMessage(String userId) {
    User user = getUserFromUserId(userId);
    return createAdminActionsAuthorizer(user).canResendInvitationMessage();
  }

  private User getUserFromUserId(String userId) {
    UserType cognitoUser = cognitoServiceFacade.getCognitoUserById(userId);

    User user;
    if (OFFICE_ADMIN.equals(getAdminStrongestRole())) {
      user = mappingService.toUser(cognitoUser);
    } else {
      user = mappingService.toUserWithoutCwsData(cognitoUser);
    }
    return user;
  }

  private String getAdminStrongestRole() {
    return UserRolesService.getStrongestAdminRole(getCurrentUser());
  }

  private AdminActionsAuthorizer createAdminActionsAuthorizer(User user) {
    switch (getAdminStrongestRole()) {
      case STATE_ADMIN:
        return StateAdminAuthorizer.INSTANCE;
      case COUNTY_ADMIN:
        return new CountyAdminAuthorizer(user);
      case OFFICE_ADMIN:
        return new OfficeAdminAuthorizer(user);
      case CALS_ADMIN:
        return new CalsAdminAuthorizer(user);
      default:
        throw new IllegalStateException();
    }
  }

  @Autowired
  public void setCognitoServiceFacade(CognitoServiceFacade cognitoServiceFacade) {
    this.cognitoServiceFacade = cognitoServiceFacade;
  }

  @Autowired
  public void setMappingService(MappingService mappingService) {
    this.mappingService = mappingService;
  }

}
