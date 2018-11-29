package gov.ca.cwds.idm.service.role.implementor;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.idm.dto.User;
import org.junit.Test;

public class IdmJobRoleImplementorTest {

  private IdmJobRoleImplementor roleImplementor = new IdmJobRoleImplementor();

  @Test
  public void getAdminActionsAuthorizer() {
    assertEquals(IdmJobAuthorizer.class,
        roleImplementor.getAdminActionsAuthorizer(new User()).getClass());
  }

  @Test
  public void getPossibleUserRoles() {
    assertThat(roleImplementor.getPossibleUserRoles(), empty());
  }
}