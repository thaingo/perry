package gov.ca.cwds.web;

import static gov.ca.cwds.idm.BaseLiquibaseTest.CMS_STORE_URL;
import static gov.ca.cwds.idm.BaseLiquibaseTest.TOKEN_STORE_URL;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.codehaus.jackson.JsonNode;
import gov.ca.cwds.PerryApplication;
import gov.ca.cwds.idm.BaseLiquibaseTest;
import gov.ca.cwds.service.sso.OAuth2Service;
import io.dropwizard.testing.FixtureHelpers;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
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

@ActiveProfiles({"cognito, prod, nostate, mfa, test"})
@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.identityManager.idmMapping=config/idm.groovy",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
    "spring.datasource.url=" + CMS_STORE_URL,
    "perry.whiteList=*",
    "perry.identityProvider.idpMapping=config/cognito.groovy",
    "perry.serviceProviders.default.identityMapping=config/default.groovy",
    "perry.serviceProviders.intake.identityMapping=config/intake.groovy",
    "perry.jwt.timeout=10",
    "security.oauth2.resource.revokeTokenUri=http://revoke.token.url",
    "security.oauth2.resource.userInfoUri=http://user.info.url",
    "security.oauth2.resource.logoutTokenUri=http://logout.token.url"
}, classes = {PerryLoginTestConfiguration.class, PerryApplication.class})

public class PerryMFALoginTest extends BaseLiquibaseTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(PerryMFALoginTest.class);
  public static final String MFA_LOGIN_URL = "http://localhost/mfa-login.html";

  @Autowired
  private OAuth2Service oAuth2Service;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @BeforeClass
  public static void beforeClass() throws Exception {
    runLiquibaseScript(CMS_STORE_URL, "liquibase/cms-data.xml");
  }

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
        .andExpect(MockMvcResultMatchers.redirectedUrl(MFA_LOGIN_URL))
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

//    result = mockMvc
//        .perform(get("/authn/validate?token=" + token))
//        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
//        .andReturn();
//    LOGGER.info("Perry JSON: {}", result.getResponse().getContentAsString());


    Mockito.verify(oAuth2Service, Mockito.times(5)).getUserInfo(token);

  }



  @Test
  public void whenEmptyMFAJsonProvided_thenPerryErrorPage() throws Exception {

    // Trying to access secured URL
    MvcResult result = mockMvc
        .perform(get("/authn/login?callback=/demo-sp.html"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl(MFA_LOGIN_URL))
        .andReturn();
    LOGGER.info("Login page redirect: {}", result.getResponse().getRedirectedUrl());

    // Sending MFA Json to /login endpoint
    result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
        .session((MockHttpSession) result.getRequest().getSession())
        .param("CognitoResponse", "{}")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers
            .redirectedUrl("/login.html?error=true"))
        .andReturn();
    LOGGER.info("Error redirect: {}", result.getResponse().getRedirectedUrl());
  }

//  private void setMockBehaviour(OAuth2Service oAuth2Service) throws IOException {
//    String ssoToken = "eyJraWQiOiJzWUFcL1VUTGdSTis4cTJSRUxEZXdBamhGd0RWaVR2Tm1DYThlMzYrMUZwOD0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI5YTExNTg1ZC0wYTg2LTQ3MTUtYmVkZi0zY2Y3ODNiYzRiYWYiLCJkZXZpY2Vfa2V5IjoidXMtd2VzdC0yX2UyN2E2Y2IxLWVjYTQtNDQxYy1iMmQxLTRmZDZkNWExOWYwYiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE1MzA4OTkwNzMsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy13ZXN0LTIuYW1hem9uYXdzLmNvbVwvdXMtd2VzdC0yX2JVdEFTeFV6NiIsImV4cCI6MTUzMDkwMjY3MywiaWF0IjoxNTMwODk5MDczLCJqdGkiOiIwNDBlNTg4MS0yMzY5LTQ2MmEtOTYzNS00MmQ0ZDI2YThkODYiLCJjbGllbnRfaWQiOiIyYTFkZjF2OGJyNjBpNTJxb2ZpNHFta2oyayIsInVzZXJuYW1lIjoiOWExMTU4NWQtMGE4Ni00NzE1LWJlZGYtM2NmNzgzYmM0YmFmIn0.MTvqzH5DtLlNkJIv6fP0DbL62jW2Dv6Yca1l-XBVQ8gLEwtZyrva5rqtH1W-wXSzmzCkJ8WRm3JjrvayJZa5yjy4HjIfPumh4mfQOm5XFO3RR8GGBjPv5wl6aCzcZAX6Stxk88XoqlrasneqzAMo1L9xeTrKQe4UZ8ame-RiHfGAy-if2C4dNMbrX9SfYqqf0DDyW-dG83ijQJ-dzL4Hjim0YYXJ1jV43oRvv4R6dz3RnyYu9KxOTUjf9QLuIjuoKQxil0mhPavOyCMARin2kfzdGMg2Tp5ETJWW0LyetLGYvOHSUOJ2Lp2njZH1H92z5IjI4UQu5zC11tFoheN5DQ";
//    Mockito.when(oAuth2Service.getUserInfo(ssoToken)).thenReturn(constructUserInfo());
//  }

}
