package gov.ca.cwds.idm.util;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.EXTERNAL_APP;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USERPOOL;
import static gov.ca.cwds.idm.util.TestUtils.generateId;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.ADMIN_OFFICE_IDS_PARAM;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.COUNTY_NAME_PARAM;
import static gov.ca.cwds.util.Utils.toSet;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.CognitoProperties;
import java.util.Set;

public final class TestHelper {

  public static final String COUNTY_NAME = "Yolo";
  public static final String OFFICE_ID = "Yolo_2";
  public static final String ADMIN_ID = "adminId";
  public static final String USER_ID = "userId";

  private TestHelper() {
  }

  public static User user(String countyName, String officeId) {
    User user = new User();
    user.setId(USER_ID);
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
    user.setId(generateId());
    user.setEmail("gonzales@gmail.com");
    user.setFirstName("Garcia");
    user.setLastName("Gonzales");
    user.setCountyName("Yolo");
    user.setEnabled(Boolean.TRUE);
    user.setRoles(toSet(CWS_WORKER));
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
    admin.setUserId(ADMIN_ID);
    admin.setRoles(roles);
    admin.setParameter(COUNTY_NAME_PARAM, countyName);
    admin.setParameter(ADMIN_OFFICE_IDS_PARAM, adminOfficeIds);
    return admin;
  }

  public static UniversalUserToken idmJob() {
    UniversalUserToken admin = new UniversalUserToken();
    admin.setRoles(toSet(EXTERNAL_APP));
    return admin;
  }

  public static CognitoProperties getTestCognitoProperties() {
    CognitoProperties properties = new CognitoProperties();
    properties.setIamAccessKeyId("iamAccessKeyId");
    properties.setIamSecretKey("iamSecretKey");
    properties.setUserpool(USERPOOL);
    properties.setRegion("us-east-2");
    return properties;
  }

  public static User superAdmin() {
    return user(toSet(SUPER_ADMIN), COUNTY_NAME, OFFICE_ID);
  }

  public static User stateAdmin() {
    return user(toSet(STATE_ADMIN), COUNTY_NAME, OFFICE_ID);
  }

  public static User countyAdmin() {
    return user(toSet(COUNTY_ADMIN), COUNTY_NAME, OFFICE_ID);
  }

  public static User officeAdmin() {
    return user(toSet(OFFICE_ADMIN), COUNTY_NAME, OFFICE_ID);
  }

  public static User cwsWorker() {
    return TestHelper.user(toSet(CWS_WORKER), COUNTY_NAME, OFFICE_ID);
  }

  public static User calsWorker() {
    return TestHelper.user(toSet(CALS_EXTERNAL_WORKER), COUNTY_NAME, OFFICE_ID);
  }
}
