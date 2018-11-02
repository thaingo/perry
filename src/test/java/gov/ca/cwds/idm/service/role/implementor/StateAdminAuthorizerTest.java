package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.prepareCalsAdmin;
import static gov.ca.cwds.idm.util.TestHelper.prepareCountyAdmin;
import static gov.ca.cwds.idm.util.TestHelper.prepareOfficeAdmin;
import static gov.ca.cwds.idm.util.TestHelper.prepareStateAdmin;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "gov.ca.cwds.util.CurrentAuthenticatedUserUtil")
public class StateAdminAuthorizerTest {

  @Test
  public void canNotStateAdminEditRoles() {
    assertFalse(new StateAdminAuthorizer(prepareStateAdmin()).canEditRoles());
  }

  @Test
  public void canEditCountyAdminRoles() {
    assertTrue(new StateAdminAuthorizer(prepareCountyAdmin()).canEditRoles());
  }

  @Test
  public void canEditOfficeAdminRoles() {
    assertTrue(new StateAdminAuthorizer(prepareOfficeAdmin()).canEditRoles());
  }

  @Test
  public void canEditCalsAdminRoles() {
    assertFalse(new StateAdminAuthorizer(prepareCalsAdmin()).canEditRoles());
  }

  @Before
  public void mockStateAdmin() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    when(getCurrentUser())
        .thenReturn(admin(toSet(STATE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
  }

}