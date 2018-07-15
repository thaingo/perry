package gov.ca.cwds.web;

import static gov.ca.cwds.idm.BaseLiquibaseTest.CMS_STORE_URL;
import static gov.ca.cwds.idm.BaseLiquibaseTest.TOKEN_STORE_URL;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import gov.ca.cwds.idm.BaseLiquibaseTest;
import gov.ca.cwds.service.sso.custom.form.FormService;
import io.dropwizard.testing.FixtureHelpers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles({"dev", "test"})
@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.identityManager.idmMapping=config/idm.groovy",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
    "spring.datasource.url=" + CMS_STORE_URL,
    "perry.whiteList=" + "/demo-sp.html",
    "perry.identityProvider.idpMapping=config/cognito.groovy",
    "perry.serviceProviders.mfa.identityMapping=config/default.groovy"
})
public class PerryLoginTest extends BaseLiquibaseTest {

  @Autowired
  private FormService formService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @Before
  public void before() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity()).build();
  }

  @Test
  public void whenLoginJsonProvided_thenRedirect() throws Exception {
    MvcResult result = mockMvc
        .perform(get("/authn/login?callback=/demo-sp.html"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

    MockHttpSession session = (MockHttpSession) result.getRequest().getSession();

    System.out.println("getRedirectedUrl: [" + result.getResponse().getRedirectedUrl() + "]");

    result = mockMvc.perform(MockMvcRequestBuilders.post("/login").session(session)
        .param("CognitoResponse", FixtureHelpers.fixture("fixtures/mfa/mfa-response.json"))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

    System.out.println("getRedirectedUrl: [" + result.getResponse().getRedirectedUrl() + "]");

    session = (MockHttpSession) result.getRequest().getSession();

    result = mockMvc
        .perform(get(result.getResponse().getRedirectedUrl()).session(session))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

    System.out.println("getRedirectedUrl: [" + result.getResponse().getRedirectedUrl() + "]");
  }
}
