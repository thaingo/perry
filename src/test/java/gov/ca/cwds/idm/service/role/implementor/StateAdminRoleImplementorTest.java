package gov.ca.cwds.idm.service.role.implementor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;
import java.util.Arrays;
import org.junit.Test;

public class StateAdminRoleImplementorTest {

  private StateAdminRoleImplementor stateAdminRoleImplementor = new StateAdminRoleImplementor();

  @Test
  public void getAdminActionsAuthorizer() {
    assertEquals(StateAdminAuthorizer.class,
        stateAdminRoleImplementor.getAdminActionsAuthorizer(new User()).getClass());
  }

  @Test
  public void getPossibleUserRoles() {
    assertArrayEquals(
        Arrays.asList(Roles.OFFICE_ADMIN, Roles.COUNTY_ADMIN, Roles.CWS_WORKER).toArray(),
        stateAdminRoleImplementor.getPossibleUserRoles().toArray());
  }

}