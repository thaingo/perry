package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;

import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class AdminOfficesIdmResourceTest extends IdmResourceTest {

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
}
