package gov.ca.cwds.idm.service.role.implementor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;
import org.junit.Test;

public class CountyAdminRoleImplementorTest {

  private CountyAdminRoleImplementor countyAdminRoleImplementor = new CountyAdminRoleImplementor();

  @Test
  public void getAdminActionsAuthorizer() {
    assertEquals(CountyAdminAuthorizer.class,
        countyAdminRoleImplementor.getAdminActionsAuthorizer(new User()).getClass());
  }

  @Test
  public void getPossibleUserRoles() {
    assertArrayEquals(new String[]{Roles.OFFICE_ADMIN, Roles.CWS_WORKER},
        countyAdminRoleImplementor.getPossibleUserRoles().toArray());
  }
}