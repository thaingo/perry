package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.ADMIN_ID;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.calsWorker;
import static gov.ca.cwds.idm.util.TestHelper.countyAdmin;
import static gov.ca.cwds.idm.util.TestHelper.cwsWorker;
import static gov.ca.cwds.idm.util.TestHelper.officeAdmin;
import static gov.ca.cwds.idm.util.TestHelper.stateAdmin;
import static gov.ca.cwds.idm.util.TestHelper.superAdmin;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.Utils.toSet;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import org.junit.Before;
import org.junit.Test;

public class SuperAdminAuthorizerTest extends BaseAuthorizerTest {

  @Override
  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return new SuperAdminAuthorizer(user);
  }

  @Before
  public void before() {
    when(getCurrentUser()).thenReturn(admin(toSet(SUPER_ADMIN), ADMIN_COUNTY, toSet(ADMIN_OFFICE)));
    when(getCurrentUserCountyName()).thenReturn(ADMIN_COUNTY);
  }

  @Test
  public void canViewSuperAdmin() {
    canView(superAdmin());
  }

  @Test
  public void canViewHimself() {
    User user = superAdmin();
    user.setId(ADMIN_ID);
    canView(superAdmin());
  }

  @Test
  public void canViewStateAdmin() {
    canView(stateAdmin());
  }

  @Test
  public void canViewCountyAdmin() {
    canView(countyAdmin());
  }

  @Test
  public void canViewOfficeAdmin() {
    canView(officeAdmin());
  }

  @Test
  public void canViewCwsWorker() {
    canView(cwsWorker());
  }

  @Test
  public void canViewCalsWorker() {
    canView(calsWorker());
  }

  @Test
  public void canCreateSuperAdmin() {
    canCreateInAnyCountyAndOffice(SUPER_ADMIN);
  }

  @Test
  public void canCreateStateAdmin() {
    canCreateInAnyCountyAndOffice(STATE_ADMIN);
  }

  @Test
  public void canCreateCountyAdmin() {
    canCreateInAnyCountyAndOffice(COUNTY_ADMIN);
  }

  @Test
  public void canCreateOfficeAdmin() {
    canCreateInAnyCountyAndOffice(OFFICE_ADMIN);
  }

  @Test
  public void canCreateCwsWorker() {
    canCreateInAnyCountyAndOffice(CWS_WORKER);
  }

  @Test
  public void canCreateCalsWorker() {
    canCreateInAnyCountyAndOffice(CALS_EXTERNAL_WORKER);
  }

  @Test
  public void canUpdateSuperAdmin() {
    canUpdate(superAdmin());
  }

  @Test
  public void canUpdateStateAdmin() {
    canUpdate(stateAdmin());
  }

  @Test
  public void canUpdateCountyAdmin() {
    canUpdate(countyAdmin());
  }

  @Test
  public void canUpdateOfficeAdmin() {
    canUpdate(officeAdmin());
  }

  @Test
  public void canUpdateCwsWorker() {
    canUpdate(cwsWorker());
  }

  @Test
  public void canUpdateCalsWorker() {
    canUpdate(calsWorker());
  }

  @Test
  public void canUpdateCalsExternalWorkerRole() {
    canUpdateCalsExternalWorkerInEveryCountyTo(CALS_EXTERNAL_WORKER);
    canUpdateCalsExternalWorkerInEveryCountyTo(CWS_WORKER);
    canUpdateCalsExternalWorkerInEveryCountyTo(OFFICE_ADMIN);
    canUpdateCalsExternalWorkerInEveryCountyTo(COUNTY_ADMIN);
    canUpdateCalsExternalWorkerInEveryCountyTo(STATE_ADMIN);
    canUpdateCalsExternalWorkerInEveryCountyTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateCwsWorkerRoles() {
    canUpdateCwsWorkerInEveryCountyTo(CALS_EXTERNAL_WORKER);
    canUpdateCwsWorkerInEveryCountyTo(CWS_WORKER);
    canUpdateCwsWorkerInEveryCountyTo(OFFICE_ADMIN);
    canUpdateCwsWorkerInEveryCountyTo(COUNTY_ADMIN);
    canUpdateCwsWorkerInEveryCountyTo(STATE_ADMIN);
    canUpdateCwsWorkerInEveryCountyTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateOfficeAdminRole() {
    canUpdateOfficeAdminInEveryCountyTo(CALS_EXTERNAL_WORKER);
    canUpdateOfficeAdminInEveryCountyTo(CWS_WORKER);
    canUpdateOfficeAdminInEveryCountyTo(OFFICE_ADMIN);
    canUpdateOfficeAdminInEveryCountyTo(COUNTY_ADMIN);
    canUpdateOfficeAdminInEveryCountyTo(STATE_ADMIN);
    canUpdateOfficeAdminInEveryCountyTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateCountyAdminRole() {
    canUpdateCountyAdminInEveryCountyTo(CALS_EXTERNAL_WORKER);
    canUpdateCountyAdminInEveryCountyTo(CWS_WORKER);
    canUpdateCountyAdminInEveryCountyTo(OFFICE_ADMIN);
    canUpdateCountyAdminInEveryCountyTo(COUNTY_ADMIN);
    canUpdateCountyAdminInEveryCountyTo(STATE_ADMIN);
    canUpdateCountyAdminInEveryCountyTo(SUPER_ADMIN);
  }

  @Test
  public void canUpdateStateAdminRole() {
    canUpdateStateAdminInEveryCountyTo(CALS_EXTERNAL_WORKER);
    canUpdateStateAdminInEveryCountyTo(CWS_WORKER);
    canUpdateStateAdminInEveryCountyTo(OFFICE_ADMIN);
    canUpdateStateAdminInEveryCountyTo(COUNTY_ADMIN);
    canUpdateStateAdminInEveryCountyTo(STATE_ADMIN);
    canUpdateStateAdminInEveryCountyTo(SUPER_ADMIN);
  }
}