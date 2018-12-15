package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.calsAdmin;
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
    when(getCurrentUser()).thenReturn(admin(toSet(SUPER_ADMIN),null, null));
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
  public void canEditCalsAdminRoles() {
    assertCanEditRoles(calsAdmin());
  }

  @Test
  public void canEditCwsWorkerRoles() {
    assertCanEditRoles(cwsWorker());
  }

  @Test
  public void canEditCalsWorkerRoles() {
    assertCanEditRoles(calsWorker());
  }
}