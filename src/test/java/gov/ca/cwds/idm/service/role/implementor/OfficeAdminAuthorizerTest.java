package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.util.TestHelper;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "gov.ca.cwds.util.CurrentAuthenticatedUserUtil")
public class OfficeAdminAuthorizerTest {

  @Before
  public void before() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
  }

  @Test
  public void canNotEditStateAdminTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
    assertFalse(
        new OfficeAdminAuthorizer(TestHelper.user(toSet(STATE_ADMIN),
            "Yolo", "Yolo_2")).canUpdateUser());
  }

  @Test
  public void canEditCwsWorkerTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    assertTrue(new OfficeAdminAuthorizer(TestHelper.user(toSet(CWS_WORKER),
        "Yolo", "Yolo_2")).canUpdateUser());
  }

  @Test
  public void cantEditCountyAdminTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    assertFalse(
        new OfficeAdminAuthorizer(TestHelper.user(toSet(COUNTY_ADMIN),
            "Yolo", "Yolo_2")).canUpdateUser());
  }

  @Test
  public void cantEditOfficeAdminTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
    assertFalse(
        new OfficeAdminAuthorizer(TestHelper.user(toSet(OFFICE_ADMIN),
            "Yolo", "Yolo_2")).canUpdateUser());
  }

}