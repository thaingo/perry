package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;

import gov.ca.cwds.idm.dto.User;

final class AuthorizationUtils {

  private AuthorizationUtils() {
  }

  static boolean isPrincipalInTheSameCountyWith(User user) {
    String userCountyName = user.getCountyName();
    String adminCountyName = getCurrentUserCountyName();
    return userCountyName != null && userCountyName.equals(adminCountyName);
  }

}
