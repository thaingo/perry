package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;

import org.junit.Test;

public class RolesIdmResourceTest extends IdmResourceTest {

  @Test
  @WithMockCustomUser
  public void testGetRoles() throws Exception {
    assertGetRolesSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testGetRolesWithOtherRole() throws Exception {
    assertGetRolesUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testGetRolesStateAdmin() throws Exception {
    assertGetRolesSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testGetRolesCalsAdmin() throws Exception {
    assertGetRolesSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetRolesOfficeAdmin() throws Exception {
    assertGetRolesSuccess();
  }
}
