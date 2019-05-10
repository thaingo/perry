package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.ADMIN_ID;
import static gov.ca.cwds.idm.util.TestHelper.COUNTY_NAME;
import static gov.ca.cwds.idm.util.TestHelper.OFFICE_ID;
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
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class SuperAdminAuthorizerTest extends BaseAuthorizerTest {

  @Override
  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return new SuperAdminAuthorizer(user);
  }

  @Before
  public void before() {
    when(getCurrentUser()).thenReturn(admin(toSet(SUPER_ADMIN), COUNTY_NAME, toSet(OFFICE_ID)));
    when(getCurrentUserCountyName()).thenReturn(null);
  }

  @Test
  public void canEditSuperAdminRoles() {
    assertCanEditRoles(superAdmin());
  }

  @Test
  public void canEditStateAdminRoles() {
    assertCanEditRoles(stateAdmin());
  }

  @Test
  public void canEditCountyAdminRoles() {
    assertCanEditRoles(countyAdmin());
  }

  @Test
  public void canEditOfficeAdminRoles() {
    assertCanEditRoles(officeAdmin());
  }

  @Test
  public void canEditCwsWorkerRoles() {
    assertCanEditRoles(cwsWorker());
  }

  @Test
  public void canEditCalsWorkerRoles() {
    assertCanEditRoles(calsWorker());
  }

  @Test
  public void canViewSuperAdmin() {
    assertCanViewUser(superAdmin());
  }

  @Test
  public void canViewHimself() {
    User user = superAdmin();
    user.setId(ADMIN_ID);
    assertCanViewUser(superAdmin());
  }

  @Test
  public void canViewStateAdmin() {
    assertCanViewUser(stateAdmin());
  }

  @Test
  public void canViewCountyAdmin() {
    assertCanViewUser(countyAdmin());
  }

  @Test
  public void canViewOfficeAdmin() {
    assertCanViewUser(officeAdmin());
  }

  @Test
  public void canViewCwsWorker() {
    assertCanViewUser(cwsWorker());
  }

  @Test
  public void canViewCalsWorker() {
    assertCanViewUser(calsWorker());
  }

  @Test
  public void canCreateSuperAdmin() {
    assertCanCreateUser(superAdmin());
  }

  @Test
  public void canCreateStateAdmin() {
    assertCanCreateUser(stateAdmin());
  }

  @Test
  public void canCreateCountyAdmin() {
    assertCanCreateUser(countyAdmin());
  }

  @Test
  public void canCreateOfficeAdmin() {
    assertCanCreateUser(officeAdmin());
  }

  @Test
  public void canCreateCwsWorker() {
    assertCanCreateUser(cwsWorker());
  }

  @Test
  public void canCreateCalsWorker() {
    assertCanCreateUser(calsWorker());
  }

  @Test
  public void canUpdateSuperAdmin() {
    assertCanUpdateUser(superAdmin());
  }

  @Test
  public void canUpdateStateAdmin() {
    assertCanUpdateUser(stateAdmin());
  }

  @Test
  public void canUpdateCountyAdmin() {
    assertCanUpdateUser(countyAdmin());
  }

  @Test
  public void canUpdateOfficeAdmin() {
    assertCanUpdateUser(officeAdmin());
  }

  @Test
  public void canUpdateCwsWorker() {
    assertCanUpdateUser(cwsWorker());
  }

  @Test
  public void canUpdateCalsWorker() {
    assertCanUpdateUser(calsWorker());
  }

  @Test
  public void testGetPossibleUserRolesAtCreate() {
    assertEquals(
        Arrays.asList(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER,
            CALS_EXTERNAL_WORKER),
        getAuthorizer(cwsWorker()).getPossibleUserRolesAtCreate());
  }
}