package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.calsAdmin;
import static gov.ca.cwds.idm.util.TestHelper.countyAdmin;
import static gov.ca.cwds.idm.util.TestHelper.officeAdmin;
import static gov.ca.cwds.idm.util.TestHelper.stateAdmin;
import static gov.ca.cwds.service.messages.MessageCode.CALS_ADMIN_ROLES_CANNOT_BE_EDITED;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_EDIT_ROLES_OF_OTHER_COUNTY_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.STATE_ADMIN_ROLES_CANNOT_BE_EDITED;
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
        "Yolo", toSet("Yolo_2")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
  }

  @Override
  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return new CountyAdminAuthorizer(user);
  }

  @Test
  public void canNotEditStateAdminRoles() {
    assertCanNotEditRoles(stateAdmin(), STATE_ADMIN_ROLES_CANNOT_BE_EDITED);
  }

  @Test
  public void canNotEditCountyAdminRoles() {
    assertCanNotEditRoles(countyAdmin(),
        COUNTY_ADMIN_CANNOT_EDIT_ROLES_OF_OTHER_COUNTY_ADMIN);
  }

  @Test
  public void canEditOfficeAdminRoles() {
    assertCanEditRoles(officeAdmin());
  }

  @Test
  public void canEditCalsAdminRoles() {
    assertCanNotEditRoles(calsAdmin(), CALS_ADMIN_ROLES_CANNOT_BE_EDITED);
  }
}