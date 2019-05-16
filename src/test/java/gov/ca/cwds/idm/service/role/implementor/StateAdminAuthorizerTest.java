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
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.STATE_ADMIN_ROLES_CANNOT_BE_EDITED;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.Utils.toSet;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import org.junit.Before;
import org.junit.Test;

public class StateAdminAuthorizerTest extends BaseAuthorizerTest {

  @Override
  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return new StateAdminAuthorizer(user);
  }

  @Before
  public void before() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(STATE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
  }

  @Test
  public void canNotViewSuperAdmin() {
    canNotView(superAdmin(), NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Test
  public void canUpdateStateAdminInEveryCounty() {
    canUpdateInEveryCounty(STATE_ADMIN);
  }

  @Test
  public void canUpdateCountyAdminInEveryCounty() {
    canUpdateInEveryCounty(COUNTY_ADMIN);
  }

  @Test
  public void canUpdateOfficeAdminInEveryCounty() {
    canUpdateInEveryCounty(OFFICE_ADMIN);
  }

  @Test
  public void canUpdateCwsWorkerInEveryCounty() {
    canUpdateInEveryCounty(CWS_WORKER);
  }

  @Test
  public void canUpdateCalsExternalWorkerInEveryCounty() {
    canUpdateInEveryCounty(CALS_EXTERNAL_WORKER);
  }

  @Test
  public void canNotUpdateSuperAdmin() {
    canNotUpdateWithAuthorizationError(superAdmin(),
        NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Test
  public void canCreateCwsWorker() {
    canCreateInAnyCountyAndOffice(CWS_WORKER);
  }

  @Test
  public void canCreateOfficeAdmin() {
    canCreateInAnyCountyAndOffice(OFFICE_ADMIN);
  }

  @Test
  public void canCreateCountyAdmin() {
    canCreateInAnyCountyAndOffice(COUNTY_ADMIN);
  }

  @Test
  public void canNotCreateSuperAdmin() {
    canNotCreateInAnyCountyAndOffice(SUPER_ADMIN);
  }

  @Test
  public void canNotCreateCalsExternalWorker() {
    canNotCreateInAnyCountyAndOffice(CALS_EXTERNAL_WORKER);
  }

  @Test
  public void canUpdateCalsExternalWorkerRole() {
    canUpdateCalsExternalWorkerInEveryCountyTo(CALS_EXTERNAL_WORKER);
    canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(CWS_WORKER);
    canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(OFFICE_ADMIN);
    canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(COUNTY_ADMIN);
    canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(STATE_ADMIN);
    canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateCwsWorkerRoles() {
    canNotUpdateCwsWorkerInSameOfficeTo(CALS_EXTERNAL_WORKER);
    canUpdateCwsWorkerInEveryCountyTo(CWS_WORKER);
    canUpdateCwsWorkerInEveryCountyTo(OFFICE_ADMIN);
    canUpdateCwsWorkerInEveryCountyTo(COUNTY_ADMIN);
    canNotUpdateCwsWorkerInSameOfficeTo(STATE_ADMIN);
    canNotUpdateCwsWorkerInSameOfficeTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateOfficeAdminRole() {
    canNotUpdateOfficeAdminInSameOfficeTo(CALS_EXTERNAL_WORKER);
    canUpdateOfficeAdminInEveryCountyTo(CWS_WORKER);
    canUpdateOfficeAdminInEveryCountyTo(OFFICE_ADMIN);
    canUpdateOfficeAdminInEveryCountyTo(COUNTY_ADMIN);
    canNotUpdateOfficeAdminInSameOfficeTo(STATE_ADMIN);
    canNotUpdateOfficeAdminInSameOfficeTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateCountyAdminRole() {
    canNotUpdateCountyAdminRoleInSameOfficeTo(CALS_EXTERNAL_WORKER);
    canUpdateCountyAdminInEveryCountyTo(CWS_WORKER);
    canUpdateCountyAdminInEveryCountyTo(OFFICE_ADMIN);
    canUpdateCountyAdminInEveryCountyTo(COUNTY_ADMIN);
    canNotUpdateCountyAdminRoleInSameOfficeTo(STATE_ADMIN);
    canNotUpdateCountyAdminRoleInSameOfficeTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateStateAdminRole() {
    canNotUpdateStateAdminInSameOfficeTo(CALS_EXTERNAL_WORKER);
    canNotUpdateStateAdminInSameOfficeTo(CWS_WORKER);
    canNotUpdateStateAdminInSameOfficeTo(OFFICE_ADMIN);
    canNotUpdateStateAdminInSameOfficeTo(COUNTY_ADMIN);
    canUpdateStateAdminInEveryCountyTo(STATE_ADMIN);
    canNotUpdateStateAdminInSameOfficeTo(SUPER_ADMIN);
  }

  private void canNotCreateInAnyCountyAndOffice(String userRole) {
    canNotCreateWithValidationError(user(toSet(userRole), "SomeCounty", "someOffice"),
        UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES);
  }

  private void canNotUpdateStateAdminInSameOfficeTo(String newUserRole) {
    canNotUpdateToRoleWithAuthorizationError(
        user(toSet(STATE_ADMIN), ADMIN_COUNTY, ADMIN_OFFICE),
        STATE_ADMIN_ROLES_CANNOT_BE_EDITED, newUserRole);
  }

  private void canUpdateInEveryCounty(String currentRole) {
    canUpdate(user(toSet(currentRole), "SomeCounty", "SomeOffice"));
  }
}