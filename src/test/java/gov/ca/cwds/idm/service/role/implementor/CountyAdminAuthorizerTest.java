package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
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
public class CountyAdminAuthorizerTest {

  @Test
  public void canNotEditStateAdminRoles() {
    assertFalse(new CountyAdminAuthorizer(prepareStateAdmin()).canEditRoles());
  }

  @Test
  public void canNotEditCountyAdminRoles() {
    assertFalse(new CountyAdminAuthorizer(prepareCountyAdmin()).canEditRoles());
  }

  @Test
  public void canEditOfficeAdminRoles() {
    assertTrue(new CountyAdminAuthorizer(prepareOfficeAdmin()).canEditRoles());
  }

  @Test
  public void canEditCalsAdminRoles() {
    assertTrue(new CountyAdminAuthorizer(prepareCalsAdmin()).canEditRoles());
  }

  @Before
  public void mockCountyAdmin() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    when(getCurrentUser()).thenReturn(admin(toSet(COUNTY_ADMIN),
        "Yolo", toSet("Yolo_2")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
  }

}