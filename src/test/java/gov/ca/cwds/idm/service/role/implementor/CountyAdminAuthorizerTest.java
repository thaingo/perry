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
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.Utils.toSet;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import org.junit.Before;
import org.junit.Test;

public class CountyAdminAuthorizerTest extends BaseAuthorizerTest {

  @Before
  public void mockCountyAdmin() {
    when(getCurrentUser()).thenReturn(admin(toSet(COUNTY_ADMIN),
        ADMIN_COUNTY, toSet(ADMIN_OFFICE)));
    when(getCurrentUserCountyName()).thenReturn(ADMIN_COUNTY);
  }

  @Override
  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return new CountyAdminAuthorizer(user);
  }

  @Test
  public void canNotViewSuperAdmin() {
    canNotView(superAdmin(), NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Test
  public void canNotUpdateSuperAdmin() {
    canNotUpdateWithAuthorizationError(superAdmin(),
        NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Test
  public void canCreateCwsWorkerInSameCounty() {
    canCreate(user(toSet(CWS_WORKER), ADMIN_COUNTY, "SomeOffice"));
  }

  @Test
  public void canCreateOfficeAdminInSameCounty() {
    canCreate(user(toSet(OFFICE_ADMIN), ADMIN_COUNTY, "SomeOffice"));
  }

  @Test
  public void canNotCreateCwsWorkerInOtherCounty() {
    canNotCreateWithAuthorizationError(user(toSet(CWS_WORKER), "OtherCounty", "OtherOffice"),
        NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY);
  }

  @Test
  public void canNotCreateOfficeAdminInOtherCounty() {
    canNotCreateWithAuthorizationError(user(toSet(OFFICE_ADMIN), "OtherCounty", "OtherOffice"),
        NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY);
  }

  @Test
  public void canNotCreateStateAdminInSameCounty() {
    canNotCreateInSameCounty(STATE_ADMIN);
  }

  @Test
  public void canNotCreateSuperAdminInSameCounty() {
    canNotCreateInSameCounty(SUPER_ADMIN);
  }

  @Test
  public void canNotCreateCalsExternalWorkerInSameCounty() {
    canNotCreateInSameCounty(CALS_EXTERNAL_WORKER);
  }

  @Test
  public void canNotUpdateSuperAdminInSameCountyAndOffice() {
    canNotUpdateWithAuthorizationError(
        user(toSet(SUPER_ADMIN), ADMIN_COUNTY, ADMIN_OFFICE),
        NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Test
  public void canNotUpdateStateAdminInSameCountyAndOffice() {
    canNotUpdateWithAuthorizationError(
        user(toSet(STATE_ADMIN), ADMIN_COUNTY, ADMIN_OFFICE),
        COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN);
  }

  @Test
  public void canNotUpdateCalsExternalWorkerInOtherCounty() {
    canNotUpdateInOtherCounty(CALS_EXTERNAL_WORKER);
  }

  @Test
  public void canNotUpdateCwsWorkerInOtherCounty() {
    canNotUpdateInOtherCounty(CWS_WORKER);
  }

  @Test
  public void canNotUpdateOfficeAdminInOtherCounty() {
    canNotUpdateInOtherCounty(OFFICE_ADMIN);
  }

  @Test
  public void canNotUpdateCountyAdminInOtherCounty() {
    canNotUpdateInOtherCounty(COUNTY_ADMIN);
  }

  @Test
  public void canUpdateCalsExternalWorkerRole() {
    canUpdateCalsExternalWorkerInSameCountyTo(CALS_EXTERNAL_WORKER);
    canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(CWS_WORKER);
    canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(OFFICE_ADMIN);
    canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(COUNTY_ADMIN);
    canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(STATE_ADMIN);
    canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateCwsWorkerRole() {
    canNotUpdateCwsWorkerInSameOfficeTo(CALS_EXTERNAL_WORKER);
    canUpdateCwsWorkerInSameCountyTo(CWS_WORKER);
    canUpdateCwsWorkerInSameCountyTo(OFFICE_ADMIN);
    canNotUpdateCwsWorkerInSameOfficeTo(COUNTY_ADMIN);
    canNotUpdateCwsWorkerInSameOfficeTo(STATE_ADMIN);
    canNotUpdateCwsWorkerInSameOfficeTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateOfficeAdminRole() {
    canNotUpdateOfficeAdminInSameOfficeTo(CALS_EXTERNAL_WORKER);
    canUpdateOfficeAdminInSameCountyTo(CWS_WORKER);
    canUpdateOfficeAdminInSameCountyTo(OFFICE_ADMIN);
    canNotUpdateOfficeAdminInSameOfficeTo(COUNTY_ADMIN);
    canNotUpdateOfficeAdminInSameOfficeTo(STATE_ADMIN);
    canNotUpdateOfficeAdminInSameOfficeTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateCountyAdminRole() {
    canNotUpdateCountyAdminRoleInSameOfficeTo(CALS_EXTERNAL_WORKER);
    canUpdateCountyAdminInSameCountyTo(CWS_WORKER);
    canUpdateCountyAdminInSameCountyTo(OFFICE_ADMIN);
    canUpdateCountyAdminInSameCountyTo(COUNTY_ADMIN);
    canNotUpdateCountyAdminRoleInSameOfficeTo(STATE_ADMIN);
    canNotUpdateCountyAdminRoleInSameOfficeTo(SUPER_ADMIN);
  }

  public void canNotUpdateInOtherCounty(String role) {
    canNotUpdateWithAuthorizationError(
        user(toSet(role), "OtherCounty", "OtherOffice"),
        COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY
    );
  }

  private void canNotCreateInSameCounty(String userRole) {
    canNotCreateWithValidationError(user(toSet(userRole), ADMIN_COUNTY, "SomeOffice"),
        UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES);
  }

  private void canUpdateWithTheSameRoleInSameCounty(String role) {
    canUpdateToRole(user(toSet(role), ADMIN_COUNTY, "SomeOffice"), role);
  }

  private void canUpdateCountyAdminInSameCountyTo(String newRole) {
    canUpdateToRole(user(toSet(COUNTY_ADMIN), ADMIN_COUNTY, "SomeOffice"), newRole);
  }

  private void canUpdateOfficeAdminInSameCountyTo(String newRole) {
    canUpdateToRole(user(toSet(OFFICE_ADMIN), ADMIN_COUNTY, "SomeOffice"), newRole);
  }

  private void canUpdateCwsWorkerInSameCountyTo(String newRole) {
    canUpdateToRole(user(toSet(CWS_WORKER), ADMIN_COUNTY, "SomeOffice"), newRole);
  }

  private void canUpdateCalsExternalWorkerInSameCountyTo(String newRole) {
    canUpdateToRole(user(toSet(CALS_EXTERNAL_WORKER), ADMIN_COUNTY, "SomeOffice"), newRole);
  }
}