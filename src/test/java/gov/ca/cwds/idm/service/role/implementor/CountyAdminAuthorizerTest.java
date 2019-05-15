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
    canNotUpdateWithAuthorizationError(superAdmin(), NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
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
  public void canUpdateCalsExternalWorkerToCalsExternalWorkerInSameCounty() {
    canUpdateWithTheSameRoleInSameCounty(CALS_EXTERNAL_WORKER);
  }

  @Test
  public void canNotUpdateCalsExternalWorkerInOtherCounty() {
    canNotUpdateInOtherCounty(CALS_EXTERNAL_WORKER);
  }

  @Test
  public void canUpdateCwsWorkerToCwsWorkerInSameCounty() {
    canUpdateWithTheSameRoleInSameCounty(CWS_WORKER);
  }

  @Test
  public void canNotUpdateCwsWorkerInOtherCounty() {
    canNotUpdateInOtherCounty(CWS_WORKER);
  }

  @Test
  public void canUpdateCwsWorkerToOfficeAdminInSameCounty() {
    canUpdateToRole(user(toSet(CWS_WORKER), ADMIN_COUNTY, "SomeOffice"), OFFICE_ADMIN);
  }

  @Test
  public void canUpdateOfficeAdminToOfficeAdminInSameCounty() {
    canUpdateWithTheSameRoleInSameCounty(OFFICE_ADMIN);
  }

  @Test
  public void canNotUpdateOfficeAdminInOtherCounty() {
    canNotUpdateInOtherCounty(OFFICE_ADMIN);
  }

  @Test
  public void canUpdateOfficeAdminToCwsWorkerInSameCounty() {
    canUpdateToRole(user(toSet(OFFICE_ADMIN), ADMIN_COUNTY, "SomeOffice"), CWS_WORKER);
  }

  @Test
  public void canUpdateCountyAdminToCountyAdminInSameCounty() {
    canUpdateWithTheSameRoleInSameCounty(COUNTY_ADMIN);
  }

  @Test
  public void canNotUpdateCountyAdminInOtherCounty() {
    canNotUpdateInOtherCounty(COUNTY_ADMIN);
  }

  @Test
  public void canUpdateCountyAdminToOfficeAdminInSameCounty() {
    canUpdateToRole(user(toSet(COUNTY_ADMIN), ADMIN_COUNTY, "SomeOffice"), OFFICE_ADMIN);
  }

  @Test
  public void canUpdateCountyAdminToCwsWorkerInSameCounty() {
    canUpdateToRole(user(toSet(COUNTY_ADMIN), ADMIN_COUNTY, "SomeOffice"), CWS_WORKER);
  }

  @Test
  public void canNotChangeCalsExternalWorkerRole() {
    canNotChangeCalsExternalWorkerRoleInSameOfficeTo(CWS_WORKER);
    canNotChangeCalsExternalWorkerRoleInSameOfficeTo(OFFICE_ADMIN);
    canNotChangeCalsExternalWorkerRoleInSameOfficeTo(COUNTY_ADMIN);
    canNotChangeCalsExternalWorkerRoleInSameOfficeTo(STATE_ADMIN);
    canNotChangeCalsExternalWorkerRoleInSameOfficeTo(SUPER_ADMIN);
  }

  @Test
  public void canNotChangeOfficeAdminRole() {
    canNotChangeOfficeAdminRoleInSameOfficeTo(CALS_EXTERNAL_WORKER);
    canNotChangeOfficeAdminRoleInSameOfficeTo(COUNTY_ADMIN);
    canNotChangeOfficeAdminRoleInSameOfficeTo(STATE_ADMIN);
    canNotChangeOfficeAdminRoleInSameOfficeTo(SUPER_ADMIN);
  }

  @Test
  public void canNotChangeCountyAdminRole() {
    canNotChangeCountyAdminRoleInSameOfficeTo(CALS_EXTERNAL_WORKER);
    canNotChangeCountyAdminRoleInSameOfficeTo(STATE_ADMIN);
    canNotChangeCountyAdminRoleInSameOfficeTo(SUPER_ADMIN);
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
}