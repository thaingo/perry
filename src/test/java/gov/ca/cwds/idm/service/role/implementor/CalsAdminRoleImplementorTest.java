package gov.ca.cwds.idm.service.role.implementor;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.idm.dto.User;
import org.junit.Assert;
import org.junit.Test;

public class CalsAdminRoleImplementorTest {

  private CalsAdminRoleImplementor calsAdminRoleImplementor = new CalsAdminRoleImplementor();

  @Test
  public void getAdminActionsAuthorizer() {
    assertEquals(CalsAdminAuthorizer.class,
        calsAdminRoleImplementor.getAdminActionsAuthorizer(new User()).getClass());
  }

  @Test
  public void getPossibleUserRoles() {
    Assert.assertEquals(0, calsAdminRoleImplementor.getPossibleUserRoles().size());
  }
}