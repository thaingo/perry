package gov.ca.cwds.web;

import static gov.ca.cwds.idm.BaseLiquibaseTest.CMS_STORE_URL;
import static gov.ca.cwds.idm.BaseLiquibaseTest.TOKEN_STORE_URL;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import gov.ca.cwds.PerryApplication;
import gov.ca.cwds.idm.BaseLiquibaseTest;
import gov.ca.cwds.service.sso.custom.form.FormService;
import io.dropwizard.testing.FixtureHelpers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@ActiveProfiles({"dev", "test", "default"})
@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.identityManager.idmMapping=config/idm.groovy",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
    "spring.datasource.url=" + CMS_STORE_URL,
    "perry.whiteList=" + "/demo-sp.html",
    "perry.identityProvider.idpMapping=config/cognito.groovy",
    "perry.serviceProviders.default.identityMapping=config/dev.groovy",
    "perry.serviceProviders.mfa.identityMapping=config/default.groovy",
    "perry.jwt.timeout=10"
}, classes = {PerryLoginTestConfiguration.class, PerryApplication.class})

public class PerryMFALoginTest extends BaseLiquibaseTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(PerryMFALoginTest.class);

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
  public void whenValidMFAJsonProvided_thenAuthenticate() throws Exception {

    // Trying to access secured URL
    MvcResult result = mockMvc
        .perform(get("/authn/login?callback=/demo-sp.html"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login.html"))
        .andReturn();
    LOGGER.info("Login page redirect: {}", result.getResponse().getRedirectedUrl());

    // Sending MFA Json to /login endpoint
    result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
        .session((MockHttpSession) result.getRequest().getSession())
        .param("CognitoResponse", FixtureHelpers.fixture("fixtures/mfa/mfa-response.json"))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers
            .redirectedUrl("http://localhost/authn/login?callback=/demo-sp.html"))
        .andReturn();
    LOGGER.info("Login API redirect: {}", result.getResponse().getRedirectedUrl());

    // Receiving access code
    result = mockMvc
        .perform(get(result.getResponse().getRedirectedUrl())
            .session((MockHttpSession) result.getRequest().getSession()))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andReturn();
    String accessCodeUrl = result.getResponse().getRedirectedUrl();
    assertThat(result.getResponse().getRedirectedUrl(), containsString("accessCode="));
    String accessCode = accessCodeUrl.split("=")[1];
    LOGGER.info("Access Code URL: {}", accessCodeUrl);

    MockHttpSession httpSession = (MockHttpSession) result.getRequest().getSession();
    // Receiving Perry token
    result = mockMvc
        .perform(get("/authn/token?accessCode=" + accessCode)
            .session(httpSession))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    String token = result.getResponse().getContentAsString();
    LOGGER.info("Perry token: {}", token);
    Assert
        .assertTrue(token.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));

//    PerryTokenEntity perryTokenEntity = new PerryTokenEntity();
//    perryTokenEntity.setAccessCode(accessCode);
//    perryTokenEntity.setSsoToken(token);
//
//    Mockito.verify(formService, Mockito.times(5)).validate(perryTokenEntity);

    //Validate token and receive JSON with user information
    result = mockMvc
        .perform(get("/authn/validate?token=" + token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    LOGGER.info("Perry JSON: {}", result.getResponse().getContentAsString());
  }
}
