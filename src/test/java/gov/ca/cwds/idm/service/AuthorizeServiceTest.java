package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.AuthorizeService.areNotNullAndContains;
import static gov.ca.cwds.idm.service.AuthorizeService.areNotNullAndEquals;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.ADMIN_OFFICE_IDS_PARAM;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.COUNTY_NAME_PARAM;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class AuthorizeServiceTest {

  private AuthorizeService service;

  @Before
  public void before() {
    service = new AuthorizeService();
  }

  @Test
  public void testAreNotNullAndEquals() {
    assertTrue(areNotNullAndEquals("abc", "abc"));
    assertFalse(areNotNullAndEquals("abc", "prst"));
    assertFalse(areNotNullAndEquals("abc", null));
    assertFalse(areNotNullAndEquals(null, "abc"));
    assertFalse(areNotNullAndEquals(null, null));
  }

  @Test
  public void testAreNotNullAndContains() {
    assertTrue(areNotNullAndContains(toSet("abc", "prst"), "abc"));
    assertFalse(areNotNullAndContains(toSet("abc", "klmn"), "prst"));
    assertFalse(areNotNullAndContains(toSet("abc", null), null));
    assertFalse(areNotNullAndContains(null, "abc"));
    assertFalse(areNotNullAndContains(null, null));
  }

  @Test
  public void testByUserAndAdmin_StateAdminSameCounty() {
    User user = user("Yolo", "Yolo_1");
    assertTrue(service
        .defaultAuthorizeByUserAndAdmin(user, admin(toSet(STATE_ADMIN, OFFICE_ADMIN), "Yolo", toSet("Yolo_2"))));
  }

  @Test
  public void testByUserAndAdmin_StateAdminDifferentCounty() {
    User user = user("Madera", "Madera_1");
    assertTrue(service.defaultAuthorizeByUserAndAdmin(user, admin(toSet(STATE_ADMIN), "Yolo", null)));
  }

  @Test
  public void testByUserAndAdmin_StateAdminNoCounty() {
    User user = user("Madera", "Madera_1");
    assertTrue(service.defaultAuthorizeByUserAndAdmin(user, admin(toSet(STATE_ADMIN), null, null)));
  }

  @Test
  public void testByUserAndAdmin_CountyAdminSameCounty() {
    User user = user("Yolo", "Yolo_1");
    UniversalUserToken admin = admin(toSet(COUNTY_ADMIN, OFFICE_ADMIN), "Yolo", toSet("Yolo_2"));
    assertTrue(service.defaultAuthorizeByUserAndAdmin(user, admin));
  }

  @Test
  public void testByUserAndAdmin_CountyAdminSameCountyNoOffice() {
    User user = user("Yolo", "Yolo_1");
    UniversalUserToken admin = admin(toSet(COUNTY_ADMIN), "Yolo", null);
    assertTrue(service.defaultAuthorizeByUserAndAdmin(user, admin));
  }

  @Test
  public void testByUserAndAdmin_CountyAdminDifferentCounty() {
    User user = user("Yolo", "Yolo_1");
    UniversalUserToken admin = admin(toSet(COUNTY_ADMIN), "Madera", null);
    assertFalse(service.defaultAuthorizeByUserAndAdmin(user, admin));
  }

  @Test
  public void testByUserAndAdmin_OfficeAdminSameOffice() {
    User user = user("Yolo", "Yolo_1");
    assertTrue(service.defaultAuthorizeByUserAndAdmin(user, admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1"))));
  }

  @Test
  public void testByUserAndAdmin_OfficeAdminDifferentOffice() {
    User user = user("Yolo", "Yolo_1");
    assertFalse(service.defaultAuthorizeByUserAndAdmin(user, admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_2"))));
  }

  @Test
  public void testByUserAndAdmin_OfficeAdmin_UserNoOffice() {
    User user = user("Yolo", null);
    assertFalse(service.defaultAuthorizeByUserAndAdmin(user, admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_2"))));
  }

  @Test
  public void testCalsAdminCanView() {
    User user = withRole(Roles.CALS_EXTERNAL_WORKER);
    assertTrue(service.isCalsExternalWorker(user));
  }

  @Test
  public void testCalsAdminCanNotView() {
    User user = withRole(CWS_WORKER);
    assertFalse(service.isCalsExternalWorker(user));
  }

  @Test
  public void testFindUser_OfficeAdmin() {
    assertTrue(service.findUser(
        user(toSet(CWS_WORKER), "Yolo", "Yolo_1"),
        admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2"))));

    assertTrue(service.findUser(
        user(toSet(CWS_WORKER), "Yolo", "Yolo_3"),
        admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2"))));

    assertTrue(service.findUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_1"),
        admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2"))));

    assertTrue(service.findUser(
        user(toSet(COUNTY_ADMIN), "Yolo", "Yolo_1"),
        admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2"))));

    assertFalse(service.findUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_3"),
        admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2"))));

    assertFalse(service.findUser(
        user(toSet(COUNTY_ADMIN), "Yolo", "Yolo_3"),
        admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2"))));
  }

  private User user(String countyName, String officeId) {
    User user = new User();
    user.setCountyName(countyName);
    user.setOfficeId(officeId);
    return user;
  }

  private User user(Set<String> roles, String countyName, String officeId) {
    User user = user(countyName, officeId);
    user.setRoles(roles);
    return user;
  }

  private User withRole(String role) {
    User user = new User();
    user.getRoles().add(role);
    return user;
  }

  private UniversalUserToken admin(Set<String> roles, String countyName,
      Set<String> adminOfficeIds) {
    UniversalUserToken admin = new UniversalUserToken();
    admin.setRoles(roles);
    admin.setParameter(COUNTY_NAME_PARAM, countyName);
    admin.setParameter(ADMIN_OFFICE_IDS_PARAM, adminOfficeIds);
    return admin;
  }
}
