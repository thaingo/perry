package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.AuthorizationTestHelper.admin;
import static gov.ca.cwds.idm.service.authorization.AuthorizationTestHelper.user;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.setAdminSupplier;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import gov.ca.cwds.util.UniversalUserTokenDeserializer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class AuthorizationServiceImplTest {

  private AuthorizationServiceImpl service;

  @Before
  public void before() {
    service = new AuthorizationServiceImpl();
  }

  @Test
  public void testAdminCantUpdateHimself() {
    String adminId = "someId";
    User user = user("Yolo", "Yolo_2");
    user.setId(adminId);
    setAdminSupplier(() -> {
      UniversalUserToken admin = new UniversalUserToken();
      admin.setUserId(adminId);
      admin.setParameter(UniversalUserTokenDeserializer.USER_NAME, adminId);
      return admin;
    });
    assertFalse(service.canUpdateUser(adminId));
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

  @AfterClass
  public static void resetAdminSupplier() {
    CurrentAuthenticatedUserUtil.resetAdminSupplier();
  }

}
