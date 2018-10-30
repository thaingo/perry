package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertStrict;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class PermissionsIdmResourceTest extends IdmResourceTest {

  @Test
  @WithMockCustomUser
  public void testGetPermissions() throws Exception {
    assertGetPermissionsSuccess();
  }

  private void assertGetPermissionsSuccess() throws Exception {
    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/permissions"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertStrict(result, "fixtures/idm/permissions/valid.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testGetPermissionsWithOtherRole() throws Exception {
    assertGetPermissionsUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testGetPermissionsStateAdmin() throws Exception {
    assertGetPermissionsSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testGetPermissionsCalsAdmin() throws Exception {
    assertGetPermissionsSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetPermissionsOfficeAdmin() throws Exception {
    assertGetPermissionsSuccess();
  }
}
