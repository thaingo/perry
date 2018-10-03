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

import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import org.junit.AfterClass;
import org.junit.Test;

public class OfficeAdminAuthorizerTest {

  @Test
  public void canEditCwsWorkerTest() {
    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN),
        "Yolo", toSet("Yolo_2")));
    assertTrue(new OfficeAdminAuthorizer(user(toSet(CWS_WORKER),
        "Yolo", "Yolo_2")).canUpdateUser());
  }

  @Test
  public void canNotEditStateAdminTest() {
    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN),
        "Yolo", toSet("Yolo_2")));
    assertFalse(
        new OfficeAdminAuthorizer(user(toSet(STATE_ADMIN),
            "Yolo", "Yolo_2")).canUpdateUser());
  }

  @Test
  public void cantEditCountyAdminTest() {
    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN),
        "Yolo", toSet("Yolo_2")));
    assertFalse(
        new OfficeAdminAuthorizer(user(toSet(COUNTY_ADMIN),
            "Yolo", "Yolo_2")).canUpdateUser());
  }

  @Test
  public void cantEditOfficeAdminTest() {
    setAdminSupplier(() -> admin(toSet(OFFICE_ADMIN),
        "Yolo", toSet("Yolo_2")));
    assertFalse(
        new OfficeAdminAuthorizer(user(toSet(OFFICE_ADMIN),
            "Yolo", "Yolo_2")).canUpdateUser());
  }

  @AfterClass
  public static void resetAdminSupplier() {
    CurrentAuthenticatedUserUtil.resetAdminSupplier();
  }

}