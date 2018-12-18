package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.user;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;
import static gov.ca.cwds.util.Utils.toSet;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.service.messages.MessageCode;
import org.junit.Test;

public class OfficeAdminAuthorizerTest extends BaseAuthorizerTest {

  @Override
  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return new OfficeAdminAuthorizer(user);
  }

  @Test
  public void canEditRoles() {
    assertCanEditRoles(user("Yolo", "Yolo_1"));
  }

  @Test
  public void canNotViewCalsExternalWorkerTest() {
    assertCanNotView(OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE,
        CALS_EXTERNAL_WORKER);
  }

  @Test
  public void canNotViewCalsAdminTest() {
    assertCanNotView(OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_ADMIN_ROLE, CALS_ADMIN);
  }

  @Test
  public void canNotViewSuperAdminTest() {
    assertCanNotView(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE, SUPER_ADMIN);
  }

  private void assertCanNotView(MessageCode errorCode, String... roles) {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    assertCanNotViewUser(
        user(toSet(roles),"Yolo", "Yolo_2"),
        errorCode);
  }

  @Test
  public void canNotEditStateAdminTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    assertCanNotUpdateUser(
        user(toSet(STATE_ADMIN),"Yolo", "Yolo_2"),
        OFFICE_ADMIN_CANNOT_UPDATE_ADMIN);
  }

  @Test
  public void canEditCwsWorkerTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    assertCanUpdateUser(user(toSet(CWS_WORKER),"Yolo", "Yolo_2"));
  }

  @Test
  public void canNotEditCountyAdminTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    assertCanNotUpdateUser(
        user(toSet(COUNTY_ADMIN),"Yolo", "Yolo_2"),
        OFFICE_ADMIN_CANNOT_UPDATE_ADMIN);
  }

  @Test
  public void canNotEditOfficeAdminTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
    assertCanNotUpdateUser(
        user(toSet(OFFICE_ADMIN),"Yolo", "Yolo_2"),
        OFFICE_ADMIN_CANNOT_UPDATE_ADMIN
        );
  }
}