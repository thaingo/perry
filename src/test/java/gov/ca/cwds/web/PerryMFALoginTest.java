package gov.ca.cwds.web;

import static gov.ca.cwds.idm.BaseLiquibaseTest.CMS_STORE_URL;
import static gov.ca.cwds.idm.BaseLiquibaseTest.TOKEN_STORE_URL;
import static gov.ca.cwds.web.MockOAuth2Service.EXPECTED_SSO_TOKEN;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import gov.ca.cwds.PerryApplication;
import gov.ca.cwds.idm.BaseLiquibaseTest;
import gov.ca.cwds.service.sso.OAuth2Service;
import io.dropwizard.testing.FixtureHelpers;
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
import org.springframework.test.web.servlet.ResultMatcher;
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
    "security.oauth2.resource.logoutTokenUri=http://logout.token.url",
    "perry.idpMaxAttempts=2",
    "idpRetryTimeout=0",
    "idpValidateInterval=2"
}, classes = {PerryLoginTestConfiguration.class, PerryApplication.class})

public class PerryMFALoginTest extends BaseLiquibaseTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(PerryMFALoginTest.class);

  public static final String SECURED_RESOURCE_URL = "/authn/login?callback=/demo-sp.html";
  public static final String MFA_LOGIN_URL = "http://localhost/mfa-login.html";
  public static final String LOGIN_REDIRECT_URL = "http://localhost/authn/login?callback=/demo-sp.html";
  public static final String AUTHN_TOKEN_URL = "/authn/token?accessCode=";
  public static final String AUTHN_VALIDATE_URL = "/authn/validate?token=";
  public static final String AUTHN_LOGOUT_URL = "/authn/logout?callback=/login.html";
  public static final String ERROR_PAGE_URL = "/login.html?error=true";
  public static final String LOGOUT_REDIRECT_URL = "http://logout.token.url?response_type=code&client_id=null&redirect_uri=http://localhost/login";

  public static final String VALID_MFA_RESPONSE_JSON = "fixtures/mfa/mfa-response.json";
  public static final String MISSING_RACFID_MFA_RESPONSE_JSON = "fixtures/mfa/mfa-response-missing-racfid.json";
  public static final String KEY_INFO_MISSING_MFA_RESPONSE_JSON = "fixtures/mfa/mfa-response-key-info-missing.json";
  public static final String AUTH_JSON = "fixtures/mfa/auth.json";
  public static final String AUTH_MISSING_INFO_JSON = "fixtures/mfa/auth-missing-info.json";
  public static final String AUTH_NO_RACFID_JSON = "fixtures/mfa/auth-no-racfid.json";



  public static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";


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
    MvcResult result = navigateToSecureUrl();
    result = sendMfaJson(result, FixtureHelpers.fixture(VALID_MFA_RESPONSE_JSON),
        LOGIN_REDIRECT_URL);
    result = retrieveAccessCode(result);
    String accessCode = parseAccessCode(result);
    result = retrieveToken(result, accessCode);
    String token = result.getResponse().getContentAsString();
    LOGGER.info("Perry token: {}", token);
    Assert.assertTrue(token.matches(UUID_PATTERN));
    result = validateToken(token, MockMvcResultMatchers.status().isOk(), AUTH_JSON);
    String perryJson = result.getResponse().getContentAsString();
    LOGGER.info("Perry JSON: {}", perryJson);
    Assert.assertEquals(FixtureHelpers.fixture(AUTH_JSON), perryJson);
    // TODO: review logout flow. Validate redirect URL
    logout(result);
    // TODO: fix token removal from database
    validateToken(token, MockMvcResultMatchers.status().is4xxClientError(), AUTH_JSON);
  }

  @Test
  public void whenEmptyMFAJsonProvided_thenPerryErrorPage() throws Exception {
    MvcResult result = navigateToSecureUrl();
    sendMfaJson(result, "{}", ERROR_PAGE_URL);
  }

  @Test
  public void whenRacfidMissingMFAJsonProvided_thenLoginSuccessfully() throws Exception {
    Mockito.when(oAuth2Service.getUserInfo(EXPECTED_SSO_TOKEN))
        .thenReturn(MockOAuth2Service.constructUserInfo(MISSING_RACFID_MFA_RESPONSE_JSON));
    Mockito.doCallRealMethod().when(oAuth2Service).validate(any());
    runLoginFlow(MISSING_RACFID_MFA_RESPONSE_JSON, AUTH_NO_RACFID_JSON);
  }

  @Test
  public void whenKeyInfoMissingMFAJsonProvided_thenLoginSuccessfully() throws Exception {
    Mockito.when(oAuth2Service.getUserInfo(EXPECTED_SSO_TOKEN))
        .thenReturn(MockOAuth2Service.constructUserInfo(KEY_INFO_MISSING_MFA_RESPONSE_JSON));
    Mockito.doCallRealMethod().when(oAuth2Service).validate(any());
    runLoginFlow(KEY_INFO_MISSING_MFA_RESPONSE_JSON, AUTH_MISSING_INFO_JSON);
  }

  @Test
  public void whenInvalidMFAJsonProvided_thenPerryErrorPage() throws Exception {
    MvcResult result = navigateToSecureUrl();
    sendMfaJson(result, "Invalid JSON", ERROR_PAGE_URL);
  }

  private MvcResult runLoginFlow(String mfaJson, String authJson) throws Exception {
    MvcResult result = navigateToSecureUrl();
    result = sendMfaJson(result, FixtureHelpers.fixture(mfaJson),
        LOGIN_REDIRECT_URL);
    result = retrieveAccessCode(result);
    String accessCode = parseAccessCode(result);
    result = retrieveToken(result, accessCode);
    String token = result.getResponse().getContentAsString();
    LOGGER.info("Perry token: {}", token);
    Assert.assertTrue(token.matches(UUID_PATTERN));
    result = validateToken(token, MockMvcResultMatchers.status().isOk(), authJson);
    String perryJson = result.getResponse().getContentAsString();
    LOGGER.info("Perry JSON: {}", perryJson);
    Assert.assertEquals(FixtureHelpers.fixture(authJson), perryJson);
    return result;
  }

  private MvcResult navigateToSecureUrl() throws Exception {
    MvcResult result = mockMvc
        .perform(get(SECURED_RESOURCE_URL))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl(MFA_LOGIN_URL))
        .andReturn();
    LOGGER.info("Login page redirect: {}", result.getResponse().getRedirectedUrl());
    return result;
  }

  private void logout(MvcResult result) throws Exception {
    result = mockMvc
        .perform(get(AUTHN_LOGOUT_URL)
            .session((MockHttpSession) result.getRequest().getSession()))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers
            .redirectedUrl(
                LOGOUT_REDIRECT_URL))
        .andReturn();
    LOGGER.info("Logout redirect URL: {}", result.getResponse().getRedirectedUrl());
  }

  private MvcResult validateToken(String token, ResultMatcher resultMatcher, String authJson) throws Exception {
    MvcResult result;
    result = mockMvc
        .perform(get(AUTHN_VALIDATE_URL + token))
        .andExpect(resultMatcher)
        .andReturn();
    return result;
  }

  private MvcResult retrieveToken(MvcResult result, String accessCode) throws Exception {
    result = mockMvc
        .perform(get(AUTHN_TOKEN_URL + accessCode)
            .session((MockHttpSession) result.getRequest().getSession()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    return result;
  }

  private String parseAccessCode(MvcResult result) {
    String accessCodeUrl = result.getResponse().getRedirectedUrl();
    assertThat(result.getResponse().getRedirectedUrl(), containsString("accessCode="));
    String accessCode = accessCodeUrl.split("=")[1];
    LOGGER.info("Access Code URL: {}", accessCodeUrl);
    return accessCode;
  }

  private MvcResult retrieveAccessCode(MvcResult result) throws Exception {
    result = mockMvc
        .perform(get(result.getResponse().getRedirectedUrl())
            .session((MockHttpSession) result.getRequest().getSession()))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andReturn();
    return result;
  }

  private MvcResult sendMfaJson(MvcResult result, String fixture, String redirectUrl)
      throws Exception {
    result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
        .session((MockHttpSession) result.getRequest().getSession())
        .param("CognitoResponse", fixture)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers
            .redirectedUrl(redirectUrl))
        .andReturn();
    LOGGER.info("Login API redirect: {}", result.getResponse().getRedirectedUrl());
    return result;
  }
}
