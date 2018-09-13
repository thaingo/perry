package gov.ca.cwds.idm;

import static gov.ca.cwds.Constants.CMS_STORE_URL;
import static gov.ca.cwds.Constants.TOKEN_STORE_URL;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertStrict;
import static gov.ca.cwds.util.LiquibaseUtils.runLiquibaseScript;

import gov.ca.cwds.BaseIntegrationTest;
import gov.ca.cwds.Constants;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@ActiveProfiles({"dev", "idm"})
@SpringBootTest(properties = {
    "perry.identityManager.idmBasicAuthUser=" + Constants.IDM_BASIC_AUTH_USER,
    "perry.identityManager.idmBasicAuthPass=" + Constants.IDM_BASIC_AUTH_PASS,
    "perry.identityManager.idmMapping=config/idm.groovy",
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
    "spring.datasource.url=" + CMS_STORE_URL
})
public class OfficesResourceTest extends BaseIntegrationTest {

  @BeforeClass
  public static void loadOfficesToCwsCms() throws Exception {
    runLiquibaseScript(CMS_STORE_URL, "liquibase/cms-data.xml");
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
