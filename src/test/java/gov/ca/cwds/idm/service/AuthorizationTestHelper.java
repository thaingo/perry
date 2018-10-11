package gov.ca.cwds.idm.service;

import static gov.ca.cwds.util.UniversalUserTokenDeserializer.ADMIN_OFFICE_IDS_PARAM;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.COUNTY_NAME_PARAM;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import java.util.Set;

public final class AuthorizationTestHelper {

  private AuthorizationTestHelper() {
  }

  public static User user(String countyName, String officeId) {
    User user = new User();
    user.setCountyName(countyName);
    user.setOfficeId(officeId);
    return user;
  }

  public static User user(Set<String> roles, String countyName, String officeId) {
    User user = user(countyName, officeId);
    user.setRoles(roles);
    return user;
  }

  static User withRole(String role) {
    User user = new User();
    user.getRoles().add(role);
    return user;
  }

  public static UniversalUserToken admin(Set<String> roles, String countyName,
      Set<String> adminOfficeIds) {
    UniversalUserToken admin = new UniversalUserToken();
    admin.setRoles(roles);
    admin.setParameter(COUNTY_NAME_PARAM, countyName);
    admin.setParameter(ADMIN_OFFICE_IDS_PARAM, adminOfficeIds);
    return admin;
  }

}
