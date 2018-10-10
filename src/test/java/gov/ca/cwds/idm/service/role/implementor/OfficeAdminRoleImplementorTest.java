package gov.ca.cwds.idm.service.role.implementor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;
import org.junit.Test;

public class OfficeAdminRoleImplementorTest {

  private OfficeAdminRoleImplementor officeAdminRoleImplementor = new OfficeAdminRoleImplementor();

  @Test
  public void getAdminActionsAuthorizer() {
    assertEquals(OfficeAdminAuthorizer.class,
        officeAdminRoleImplementor.getAdminActionsAuthorizer(new User()).getClass());
  }

  @Test
  public void getPossibleUserRoles() {
    assertArrayEquals(new String[]{Roles.CWS_WORKER},
        officeAdminRoleImplementor.getPossibleUserRoles().toArray());

  }

}