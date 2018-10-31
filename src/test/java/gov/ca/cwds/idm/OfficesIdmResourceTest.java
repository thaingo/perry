package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertStrict;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class OfficesIdmResourceTest extends IdmResourceTest {

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testGetAdminOfficesStateAdmin() throws Exception {
    assertAllAdminOffices();
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN, COUNTY_ADMIN})
  public void testGetAdminOfficesStateAndCountyAdmin() throws Exception {
    assertAllAdminOffices();
  }

  @Test
  @WithMockCustomUser
  public void testGetAdminOfficesCountyAdmin() throws Exception {
    assertCountyAdminOffices();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetAdminOfficesOfficeAdmin() throws Exception {
    assertCountyAdminOffices();
  }

  @Test
  @WithMockCustomUser(roles = {COUNTY_ADMIN, OFFICE_ADMIN})
  public void testGetAdminOfficesCountyAndOfficeAdmin() throws Exception {
    assertCountyAdminOffices();
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testGetAdminOfficesCalsAdmin() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/admin-offices"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testGetOffices() throws Exception {

    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/offices"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    assertStrict(result, "fixtures/idm/offices/offices.json");
  }
}
