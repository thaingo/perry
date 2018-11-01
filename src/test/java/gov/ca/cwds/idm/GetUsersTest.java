package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.SOME_PAGINATION_TOKEN;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;

import gov.ca.cwds.idm.util.WithMockCustomUser;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class GetUsersTest extends BaseIdmResourceTest {

  @Test
  public void testGetUsers() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users")
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/get-users/all-valid.json");
  }

  @Test
  public void testGetUsersPage() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users?paginationToken=" + SOME_PAGINATION_TOKEN)
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/get-users/search-valid.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testGetUsersWithOtherRole() throws Exception {
    assertGetUsersUnauthorized();
  }

  @Test
  @WithMockCustomUser()
  public void testGetUsersCountyAdmin() throws Exception {
    assertGetUsersUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testGetUsersWithStateAdmin() throws Exception {
    assertGetUsersUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testGetUsersWithCalsAdmin() throws Exception {
    assertGetUsersUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetUsersWithOfficeAdmin() throws Exception {
    assertGetUsersUnauthorized();
  }

  private void assertGetUsersUnauthorized() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }
}
