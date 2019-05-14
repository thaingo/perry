package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.superAdmin;
import static gov.ca.cwds.idm.util.TestHelper.user;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_COUNTY_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;
import static gov.ca.cwds.util.Utils.toSet;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.service.messages.MessageCode;
import org.junit.Before;
import org.junit.Test;

public class OfficeAdminAuthorizerTest extends BaseAuthorizerTest {

  public static final String ADMIN_COUNTY = "Yolo";
  public static final String ADMIN_OFFICE = "Yolo_2";

  @Before
  public void before() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            ADMIN_COUNTY, toSet(ADMIN_OFFICE)));
    when(getCurrentUserOfficeIds()).thenReturn(toSet(ADMIN_OFFICE));
    when(getCurrentUserCountyName()).thenReturn(ADMIN_COUNTY);
  }

  @Override
  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return new OfficeAdminAuthorizer(user);
  }

  @Test
  public void canNotViewCalsExternalWorkerTest() {
    assertCanNotView(OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE,
        CALS_EXTERNAL_WORKER);
  }

  private void assertCanNotView(MessageCode errorCode, String... roles) {
    canNotView(user(toSet(roles),ADMIN_COUNTY, ADMIN_OFFICE), errorCode);
  }

  @Test
  public void canCreateCwsWorkerInSameOffice() {
    canCreate(user(toSet(CWS_WORKER),ADMIN_COUNTY, ADMIN_OFFICE));
  }

  @Test
  public void canNotCreateCwsWorkerInOtherOffice() {
    canNotCreateWithAuthorizationError(user(toSet(CWS_WORKER), ADMIN_COUNTY, "otherOffice"),
        NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE);
  }

  @Test
  public void canNotCreateOfficeAdminInSameOffice() {
    canNotCreateInSameOffice(OFFICE_ADMIN);
  }

  @Test
  public void canNotCreateCountyAdminInSameOffice() {
    canNotCreateInSameOffice(COUNTY_ADMIN);
  }

  @Test
  public void canNotCreateStateAdminInSameOffice() {
    canNotCreateInSameOffice(STATE_ADMIN);
  }

  @Test
  public void canNotCreateSuperAdminInSameOffice() {
    canNotCreateInSameOffice(SUPER_ADMIN);
  }

  @Test
  public void canNotCreateCalsExternalWorkerInSameOffice() {
    canNotCreateInSameOffice(CALS_EXTERNAL_WORKER);
  }

  private void canNotCreateInSameOffice(String userRole) {
    canNotCreateWithValidationError(user(toSet(userRole), ADMIN_COUNTY, ADMIN_OFFICE),
        UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES);
  }

  @Test
  public void canNotEditStateAdminTest() {
    canNotUpdateWithAuthorizationError(
        user(toSet(STATE_ADMIN),ADMIN_COUNTY, ADMIN_OFFICE),
        OFFICE_ADMIN_CANNOT_UPDATE_STATE_ADMIN);
  }

  @Test
  public void canEditCwsWorkerTest() {
    canUpdate(user(toSet(CWS_WORKER),ADMIN_COUNTY, ADMIN_OFFICE));
  }

  @Test
  public void canNotEditCountyAdminTest() {
    canNotUpdateWithAuthorizationError(
        user(toSet(COUNTY_ADMIN),ADMIN_COUNTY, ADMIN_OFFICE),
        OFFICE_ADMIN_CANNOT_UPDATE_COUNTY_ADMIN);
  }

  @Test
  public void canNotEditOfficeAdminFromOtherOfficeTest() {
    canNotUpdateWithAuthorizationError(
        user(toSet(OFFICE_ADMIN),ADMIN_COUNTY, "Yolo_1"),
        OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE
        );
  }

  @Test
  public void canEditOfficeAdminInSameOfficeTest() {
    canUpdate(user(toSet(OFFICE_ADMIN),ADMIN_COUNTY, "Yolo_2"));
  }

  @Test
  public void canNotViewSuperAdminTest() {
    assertCanNotView(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE, SUPER_ADMIN);
  }

  @Test
  public void canNotUpdateSuperAdmin() {
    canNotUpdateWithAuthorizationError(superAdmin(),
        NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
  }
}