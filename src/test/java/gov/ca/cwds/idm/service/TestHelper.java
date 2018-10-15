package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.ADMIN_OFFICE_IDS_PARAM;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.COUNTY_NAME_PARAM;
import static gov.ca.cwds.util.Utils.toSet;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import java.util.Set;

public final class TestHelper {

  private TestHelper() {
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

  public static User user() {
    User user = new User();
    user.setEmail("gonzales@gmail.com");
    user.setFirstName("Garcia");
    user.setLastName("Gonzales");
    user.setCountyName("Yolo");
    user.setEnabled(Boolean.TRUE);
    user.setRoles(toSet(CWS_WORKER));
    return user;
  }

  public static UserType userType(User user, String userId) {
    UserType userType = new UserType();
    userType.setUsername(userId);
    userType.setEnabled(true);
    userType.setUserStatus("FORCE_CHANGE_PASSWORD");
    userType.withAttributes(CognitoUtils.buildCreateUserAttributes(user));
    return userType;
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
