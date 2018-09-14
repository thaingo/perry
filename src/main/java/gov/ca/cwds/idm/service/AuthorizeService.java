package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.isMostlyCountyAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyOfficeAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyStateAdmin;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getAdminOfficeIds;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
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
    return byUser(user);
  }

  public boolean verifyUser(User user) {
    return byUser(user);
  }

  public boolean createUser(User user) {
    return byUser(user);
  }

  public boolean updateUser(String userId) {
    UserType cognitoUser = cognitoServiceFacade.getCognitoUserById(userId);
    User user = mappingService.toUserWithoutCwsData(cognitoUser);
    return byUser(user);
  }

  private boolean byUser(User user) {
    UniversalUserToken admin = getCurrentUser();
    return byUserAndAdmin(user, admin);
  }

  boolean byUserAndAdmin(User user, UniversalUserToken admin) {
    if (isMostlyStateAdmin(admin)) {
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

  static boolean areNotNullAndEquals(String str1, String str2) {
    return str1 != null && str2 != null && str1.equals(str2);
  }

  static boolean areNotNullAndContains(Set<String> set, String str) {
    return str != null && set != null && set.contains(str);
  }

  @Autowired
  public void setCognitoServiceFacade(
      CognitoServiceFacade cognitoServiceFacade) {
    this.cognitoServiceFacade = cognitoServiceFacade;
  }

  @Autowired
  public void setMappingService(MappingService mappingService) {
    this.mappingService = mappingService;
  }
}
