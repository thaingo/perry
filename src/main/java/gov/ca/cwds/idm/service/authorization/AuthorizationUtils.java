package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;

final class AuthorizationUtils {

  private AuthorizationUtils() {
  }

  static boolean principalInTheSameCountyWith(User user) {
    UniversalUserToken admin = getCurrentUser();
    String userCountyName = user.getCountyName();
    String adminCountyName = getCountyName(admin);
    return userCountyName != null && userCountyName.equals(adminCountyName);
  }

}
