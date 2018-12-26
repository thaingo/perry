package gov.ca.cwds.web;

import static gov.ca.cwds.util.LiquibaseUtils.CMS_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.TOKEN_STORE_URL;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.BaseIntegrationTest;
import gov.ca.cwds.PerryApplication;
import gov.ca.cwds.dto.app.SystemInformationDto;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles({"dev"})
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.identityManager.idmMapping=config/idm.groovy",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
    "spring.datasource.url=" + CMS_STORE_URL,
    "perry.whiteList=*",
    "perry.identityProvider.idpMapping=config/cognito.groovy",
    "perry.serviceProviders.default.identityMapping=config/dev.groovy",
    "perry.serviceProviders.mfa.identityMapping=config/default.groovy",
    "perry.jwt.timeout=10",
    "perry.tokenRecordTimeout=240"
}, classes = {PerryApplication.class})
public class PerryDevModeTest extends BaseIntegrationTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(PerryDevModeTest.class);
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
  
  private static final String REDIS_HEALTHCHECK = "redis";

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
        .param("username", FixtureHelpers.fixture("fixtures/mfa/auth.json"))
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

    // Receiving Perry token
    result = mockMvc
        .perform(get("/authn/token?accessCode=" + accessCode)
            .session((MockHttpSession) result.getRequest().getSession()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    String token = result.getResponse().getContentAsString();
    LOGGER.info("Perry token: {}", token);
    Assert
        .assertTrue(token.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));

    //Validate token and receive JSON with user information
    result = mockMvc
        .perform(get("/authn/validate?token=" + token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(FixtureHelpers.fixture("fixtures/mfa/auth.json")))
        .andReturn();
    String perryJson = result.getResponse().getContentAsString();
    LOGGER.info("Perry JSON: {}", perryJson);

    // Logout
    result = mockMvc
        .perform(get("/authn/logout?callback=/login.html")
            .session((MockHttpSession) result.getRequest().getSession()))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers
            .redirectedUrl("/login.html"))
        .andReturn();
    LOGGER.info("Logout redirect URL: {}", result.getResponse().getRedirectedUrl());

  }

  @Test
  public void testSystemInformationResponseNoRedisProfile() throws Exception {
    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/system-information"))
            .andReturn();

    SystemInformationDto response = MAPPER
        .readValue(result.getResponse().getContentAsString(), SystemInformationDto.class);

    Assert.assertFalse(response.getHealthCheckResults().keySet().contains(REDIS_HEALTHCHECK));
  }


}
