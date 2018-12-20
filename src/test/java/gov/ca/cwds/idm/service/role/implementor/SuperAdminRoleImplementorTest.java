package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static org.junit.Assert.assertEquals;

import gov.ca.cwds.idm.dto.User;
import java.util.Arrays;
import org.junit.Test;

public class SuperAdminRoleImplementorTest {

  private SuperAdminRoleImplementor superAdminRoleImplementor = new SuperAdminRoleImplementor();

  @Test
  public void getAdminActionsAuthorizer() {
    assertEquals(SuperAdminAuthorizer.class,
        superAdminRoleImplementor.getAdminActionsAuthorizer(new User()).getClass());
  }

  @Test
  public void getPossibleUserRoles() {
    assertEquals(
        Arrays.asList(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CALS_ADMIN,
            CWS_WORKER, CALS_EXTERNAL_WORKER),
        superAdminRoleImplementor.getPossibleUserRoles());
  }
}