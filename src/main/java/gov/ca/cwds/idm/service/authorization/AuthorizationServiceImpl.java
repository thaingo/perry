package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestAdminRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCalsAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCalsExternalWorker;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isOfficeAdmin;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getAdminOfficeIds;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getUserName;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.MappingService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("authorizationService")
@Profile("idm")
public class AuthorizationServiceImpl implements AuthorizationService {

  private CognitoServiceFacade cognitoServiceFacade;

  private MappingService mappingService;

  @Override
  public boolean canFindUser(User user) {
    UniversalUserToken admin = getCurrentUser();

    return userIsInAdminManagedArea(user) ||
        isCalsAdmin(admin) && isCalsExternalWorker(user) ||
        isOfficeAdmin(admin) && areInTheSameCounty(admin, user) && !areInTheSameOffice(admin, user) && !userIsStrongerAdmin(user);
  }

  @Override
  public boolean canCreateUser(User user) {
    return userIsInAdminManagedArea(user);
  }

  @Override
  public boolean canUpdateUser(String userId) {
    //admin can't update himself
    if (userId.equals(getUserName(getCurrentUser()))) {
      return false;
    }
    User user = getUserFromUserId(userId);
    return canUpdateUser(user);
  }

  boolean canUpdateUser(User user) {
    UniversalUserToken admin = getCurrentUser();

    return
        !isOfficeAdmin(admin) && userIsInAdminManagedArea(user) ||
        isOfficeAdmin(admin) && userIsInAdminManagedArea(user) && !userIsStrongerAdmin(user) && !userIsTheSameStrengthAsAdmin(user);
  }

  @Override
  public boolean canResendInvitationMessage(String userId) {
    User user = getUserFromUserId(userId);
    return userIsInAdminManagedArea(user);
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
    UniversalUserToken admin = getCurrentUser();
    return getStrongestAdminRole(admin);
  }

  static boolean userIsInAdminManagedArea(User user) {
    UniversalUserToken admin = getCurrentUser();
    return userIsInAdminManagedArea(admin, user);
  }

  static boolean userIsInAdminManagedArea(UniversalUserToken admin, User user) {
    switch (getStrongestAdminRole(admin)) {
      case STATE_ADMIN:
        return true;
      case COUNTY_ADMIN:
        return isAdminInTheSameCountyAsUser(user);
      case OFFICE_ADMIN:
        return isAdminInTheSameOfficeAs(user);
      case CALS_ADMIN:
        return false;
      default:
        return false;
    }
  }

  private static boolean isAdminInTheSameOfficeAs(User user) {
    UniversalUserToken admin = getCurrentUser();
    String userOfficeId = user.getOfficeId();
    Set<String> adminOfficeIds = getAdminOfficeIds(admin);
    return userOfficeId != null && adminOfficeIds != null && adminOfficeIds.contains(userOfficeId);
  }

  private static boolean areInTheSameOffice(UniversalUserToken admin, User user) {
    String userOfficeId = user.getOfficeId();
    Set<String> adminOfficeIds = getAdminOfficeIds(admin);
    return userOfficeId != null && adminOfficeIds != null && adminOfficeIds.contains(userOfficeId);
  }

  static boolean isAdminInTheSameCountyAsUser(User user) {
    UniversalUserToken admin = getCurrentUser();
    return areInTheSameCounty(admin, user);
  }

  static boolean areInTheSameCounty(UniversalUserToken admin, User user) {
    String userCountyName = user.getCountyName();
    String adminCountyName = getCountyName(admin);
    return userCountyName != null && userCountyName.equals(adminCountyName);
  }

  static boolean userIsStrongerAdmin(User user) {
    UniversalUserToken admin = getCurrentUser();
    return userIsStrongerAdmin(admin, user);
  }

  static boolean userIsStrongerAdmin(UniversalUserToken admin, User user) {
    if(!isAdmin(user)) {
      return false;
    }

    String adminRole = getStrongestAdminRole(admin);
    String userRole = getStrongestAdminRole(user);

    switch (userRole) {
      case STATE_ADMIN:
        return !STATE_ADMIN.equals(adminRole);
      case COUNTY_ADMIN:
        return OFFICE_ADMIN.equals(adminRole);
      case OFFICE_ADMIN:
        return false;
      case CALS_ADMIN:
        return false;
      default:
        return false;
    }
  }

  static boolean userIsTheSameStrengthAsAdmin(User user) {
    UniversalUserToken admin = getCurrentUser();
    return userHasTheSameStrengthAsAdmin(admin, user);
  }

  static boolean userHasTheSameStrengthAsAdmin(UniversalUserToken admin, User user) {
    if(!isAdmin(user)) {
      return false;
    }

    String adminRole = getStrongestAdminRole(admin);
    String userRole = getStrongestAdminRole(user);

    return adminRole.equals(userRole);
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
