package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestCwsRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCalsAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCalsExternalWorker;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCwsAdmin;
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
import java.util.Optional;
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

    return
        userIsInAdminManagedArea(admin, user) ||
        isOfficeAdmin(admin) && areInDifferentOfficesOfTheSameCounty(user, admin) && userIsNotAStrongerAdmin(admin, user) ||
        areCalsAdminAndUser(admin, user);
  }

  @Override
  public boolean canCreateUser(User user) {
    return userIsInAdminManagedArea(getCurrentUser(), user);
  }

  @Override
  public boolean canUpdateUser(String userId) {
    UniversalUserToken admin = getCurrentUser();
    //admin can't update himself
    if (adminAndUserAreTheSamePerson(admin, userId)) {
      return false;
    }

    User user = getUserFromUserId(userId);

    return canUpdateUser(admin, user);
  }

  boolean canUpdateUser(UniversalUserToken admin, User user) {
    return
        !isOfficeAdmin(admin) && userIsInAdminManagedArea(admin, user) ||
        isOfficeAdmin(admin) && userIsInAdminManagedArea(admin, user) && userIsMoreWeakThenAdmin(admin, user);
  }

  @Override
  public boolean canResendInvitationMessage(String userId) {
    User user = getUserFromUserId(userId);
    return userIsInAdminManagedArea(getCurrentUser(), user);
  }

  private boolean adminAndUserAreTheSamePerson(UniversalUserToken admin, String userId) {
    return userId.equals(getUserName(admin));
  }

  private boolean areCalsAdminAndUser(UniversalUserToken admin, User user) {
    return isCalsAdmin(admin) && isCalsExternalWorker(user);
  }

  private static boolean userIsNotAStrongerAdmin(UniversalUserToken admin, User user) {
    return !userHasStrongerCwsRole(admin, user);
  }

  private static boolean userHasStrongerCwsRole(UniversalUserToken admin, User user) {
    if(!isCwsAdmin(user)) {
      return false;
    }
    Optional<String> optionalAdminRole = getStrongestCwsRole(admin);
    if(!optionalAdminRole.isPresent()){
      return false;
    }
    Optional<String> optionalUserRole = getStrongestCwsRole(user);
    if(!optionalUserRole.isPresent()){
      return false;
    }

    String adminRole = optionalAdminRole.get();
    String userRole = optionalUserRole.get();

    switch (userRole) {
      case STATE_ADMIN:
        return !STATE_ADMIN.equals(adminRole);
      case COUNTY_ADMIN:
        return OFFICE_ADMIN.equals(adminRole);
      default:
        return false;
    }
  }

  private boolean areInDifferentOfficesOfTheSameCounty(User user, UniversalUserToken admin) {
    return areInTheSameCounty(admin, user) && !areInTheSameOffice(admin, user);
  }

  private User getUserFromUserId(String userId) {
    UserType cognitoUser = cognitoServiceFacade.getCognitoUserById(userId);
    return mappingService.toUser(cognitoUser);
  }

  static boolean userIsInAdminManagedArea(UniversalUserToken admin, User user) {
    Optional<String> optionalAdminStrongestCwsRole = getStrongestCwsRole(admin);
    if (!optionalAdminStrongestCwsRole.isPresent()) {
      return false;
    }
    switch (optionalAdminStrongestCwsRole.get()) {
      case STATE_ADMIN:
        return true;
      case COUNTY_ADMIN:
        return isAdminInTheSameCountyAsUser(user);
      case OFFICE_ADMIN:
        return isAdminInTheSameOfficeAs(user);
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

  private static boolean isAdminInTheSameCountyAsUser(User user) {
    UniversalUserToken admin = getCurrentUser();
    return areInTheSameCounty(admin, user);
  }

  private static boolean areInTheSameCounty(UniversalUserToken admin, User user) {
    String userCountyName = user.getCountyName();
    String adminCountyName = getCountyName(admin);
    return userCountyName != null && userCountyName.equals(adminCountyName);
  }

  private static boolean userHasTheSameCwsStrengthAsAdmin(UniversalUserToken admin, User user) {
    if(!isCwsAdmin(user)) {
      return false;
    }
    Optional<String> optionalAdminRole = getStrongestCwsRole(admin);
    if(!optionalAdminRole.isPresent()){
      return false;
    }
    Optional<String> optionalUserRole = getStrongestCwsRole(user);
    if(!optionalUserRole.isPresent()){
      return false;
    }

    String adminRole = optionalAdminRole.get();
    String userRole = optionalUserRole.get();

    return adminRole.equals(userRole);
  }

  private static boolean userIsMoreWeakThenAdmin(UniversalUserToken admin, User user) {
    return userIsNotAStrongerAdmin(admin, user) && !userHasTheSameCwsStrengthAsAdmin(admin, user);
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
