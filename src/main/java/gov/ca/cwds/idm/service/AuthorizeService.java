package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.isCalsAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyCountyAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyOfficeAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyStateAdmin;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getAdminOfficeIds;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserByIdResponse;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service(value = "authorize")
@Profile("idm")
public class AuthorizeService {

  private CognitoServiceFacade cognitoServiceFacade;

  private MappingService mappingService;

  public boolean findUser(User user) {
    return authorizeByUser(user);
  }

  public Optional<MessageCode> verifyUser(User user) {
    if (!authorizeByUser(user)) {
      if (CurrentAuthenticatedUserUtil.isMostlyCountyAdmin()) {
        return Optional.of(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY);
      } else if (CurrentAuthenticatedUserUtil.isMostlyOfficeAdmin()) {
        return Optional.of(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE);
      }
    }
    return Optional.empty();
  }

  public boolean createUser(User user) {
    return authorizeByUser(user);
  }

  public boolean updateUser(String userId) {
    return authorizeByUserId(userId);
  }
  public boolean resendInvitationMessage(String userId) {
    return authorizeByUserId(userId);
  }
  private boolean authorizeByUserId(String userId) {
    return authorizeByUser(getUserFromUserId(userId));
  }

  private User getUserFromUserId(String userId) {
    UserType cognitoUser = cognitoServiceFacade.getCognitoUserById(userId);
    UniversalUserToken admin = getCurrentUser();
    User user;

    if (isMostlyOfficeAdmin(admin)) {
      user = mappingService.toUser(cognitoUser);
    } else {
      user = mappingService.toUserWithoutCwsData(cognitoUser);
    }
    return user;
  }

  private boolean authorizeByUser(User user) {
    UniversalUserToken admin = getCurrentUser();
    return byUserAndAdmin(user, admin);
  }

  boolean isCalsExternalWorker(User user) {
    return user.getRoles().contains(Roles.CALS_EXTERNAL_WORKER);
  }

  boolean byUserAndAdmin(User user, UniversalUserToken admin) {
    if (isMostlyStateAdmin(admin)  || isAuthorizedAsCalsAdmin(user, admin)) {
      return true;
    } else if (isMostlyCountyAdmin(admin)) {
      String userCountyName = user.getCountyName();
      String adminCountyName = getCountyName(admin);
      return areNotNullAndEquals(userCountyName, adminCountyName);

    } else if (isMostlyOfficeAdmin(admin)) {
      String userOfficeId = user.getOfficeId();
      Set<String> adminOfficeIds = getAdminOfficeIds(admin);
      return areNotNullAndContains(adminOfficeIds, userOfficeId);
    }
    return false;
  }

  private boolean isAuthorizedAsCalsAdmin(User user, UniversalUserToken admin) {
    return isCalsAdmin(admin) && isCalsExternalWorker(user);
  }

  static boolean areNotNullAndEquals(String str1, String str2) {
    return str1 != null && str1.equals(str2);
  }

  static boolean areNotNullAndContains(Set<String> set, String str) {
    return str != null && set != null && set.contains(str);
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
