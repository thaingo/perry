package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.setAdminSupplier;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.ADMIN_OFFICE_IDS_PARAM;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.COUNTY_NAME_PARAM;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class AuthorizationServiceTest {

  private AuthorizationServiceImpl service;

  @Before
  public void before() {
    service = new AuthorizationServiceImpl();
  }

  @Test
  public void testByUserAndAdmin_StateAdminSameCounty() {
    User user = user("Yolo", "Yolo_1");
    setAdminSupplier(() -> admin(toSet(STATE_ADMIN, OFFICE_ADMIN), "Yolo", toSet("Yolo_2")));
    assertTrue(service.canFindUser(user));
  }

  @Test
  public void testByUserAndAdmin_StateAdminDifferentCounty() {
    User user = user("Madera", "Madera_1");
    setAdminSupplier(() -> admin(toSet(STATE_ADMIN), "Yolo", null));
    assertTrue(service.canFindUser(user));
  }

  @Test
  public void testByUserAndAdmin_StateAdminNoCounty() {
    User user = user("Madera", "Madera_1");
    setAdminSupplier(() -> admin(toSet(STATE_ADMIN), null, null));
    assertTrue(service.canFindUser(user));
  }

  @Test
  public void testByUserAndAdmin_CountyAdminSameCounty() {
    User user = user("Yolo", "Yolo_1");
    setAdminSupplier(() -> admin(toSet(COUNTY_ADMIN, OFFICE_ADMIN),
        "Yolo", toSet("Yolo_2")));
    assertTrue(service.canFindUser(user));
  }

  @Test
  public void testByUserAndAdmin_CountyAdminSameCountyNoOffice() {
    User user = user("Yolo", "Yolo_1");
    setAdminSupplier(() -> admin(toSet(COUNTY_ADMIN), "Yolo", null));
    assertTrue(service.canFindUser(user));
  }

  @Test
  public void testByUserAndAdmin_CountyAdminDifferentCounty() {
    User user = user("Yolo", "Yolo_1");
    setAdminSupplier(() -> admin(toSet(COUNTY_ADMIN), "Madera", null));
    assertFalse(service.canFindUser(user));
  }

  @Test
  public void testByUserAndAdmin_OfficeAdminSameOffice() {
    User user = user("Yolo", "Yolo_1");
    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1")));
    assertTrue(
        service.canFindUser(user));
  }

  @Test
  public void testByUserAndAdmin_OfficeAdminDifferentOffice() {
    User user = user("Yolo", "Yolo_1");
    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_2")));
    assertFalse(
        service.canCreateUser(user));
  }

  @Test
  public void testByUserAndAdmin_OfficeAdmin_UserNoOffice() {
    User user = user("Yolo", null);
    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_2")));
    assertFalse(
        service.canCreateUser(user));
  }

  @Test
  public void testFindUser_OfficeAdmin() {
    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2")));
    assertTrue(service.canFindUser(user(toSet(CWS_WORKER), "Yolo", "Yolo_1")));

    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2")));
    assertTrue(service.canFindUser(
        user(toSet(CWS_WORKER), "Yolo", "Yolo_3")));

    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2")));
    assertTrue(service.canFindUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_1")));

    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2")));
    assertTrue(service.canFindUser(
        user(toSet(COUNTY_ADMIN), "Yolo", "Yolo_1")));

    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2")));
    assertFalse(service.canFindUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_3")));

    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2")));
    assertFalse(service.canFindUser(
        user(toSet(COUNTY_ADMIN), "Yolo", "Yolo_3")));
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

  @AfterClass
  public static void resetAdminSupplier() {
    CurrentAuthenticatedUserUtil.resetAdminSupplier();
  }

}
