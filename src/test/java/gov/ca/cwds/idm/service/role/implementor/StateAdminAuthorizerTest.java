package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.prepareCalsAdmin;
import static gov.ca.cwds.idm.util.TestHelper.prepareCountyAdmin;
import static gov.ca.cwds.idm.util.TestHelper.prepareOfficeAdmin;
import static gov.ca.cwds.idm.util.TestHelper.prepareStateAdmin;
import static gov.ca.cwds.service.messages.MessageCode.CALS_ADMIN_ROLES_CANNOT_BE_EDITED;
import static gov.ca.cwds.service.messages.MessageCode.STATE_ADMIN_ROLES_CANNOT_BE_EDITED;
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
  public void canNotStateAdminEditRoles() {
    assertCanNotEditRoles(prepareStateAdmin(), STATE_ADMIN_ROLES_CANNOT_BE_EDITED);
  }

  @Test
  public void canEditCountyAdminRoles() {
    assertCanEditRoles(prepareCountyAdmin());
  }

  @Test
  public void canEditOfficeAdminRoles() {
    assertCanEditRoles(prepareOfficeAdmin());
  }

  @Test
  public void canEditCalsAdminRoles() {
    assertCanNotEditRoles(prepareCalsAdmin(), CALS_ADMIN_ROLES_CANNOT_BE_EDITED);
  }
}