package gov.ca.cwds.web;

import static gov.ca.cwds.util.LiquibaseUtils.CMS_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.SPRING_BOOT_H2_PASSWORD;
import static gov.ca.cwds.util.LiquibaseUtils.SPRING_BOOT_H2_USER;
import static gov.ca.cwds.util.LiquibaseUtils.TOKEN_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.runLiquibaseScript;
import static gov.ca.cwds.util.Utils.toSet;
import static gov.ca.cwds.web.MockOAuth2Service.EXPECTED_SSO_TOKEN;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import gov.ca.cwds.BaseIntegrationTest;
import gov.ca.cwds.PerryApplication;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.NsUserService;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.service.sso.OAuth2Service;
import io.dropwizard.testing.FixtureHelpers;
import java.util.Collections;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles({"cognito, prod, nostate, mfa, test"})
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.identityManager.idmMapping=config/idm.groovy",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
    "spring.datasource.hikari.jdbcUrl=" + CMS_STORE_URL,
    "spring.datasource.hikari.username=" + SPRING_BOOT_H2_USER,
    "spring.datasource.hikari.password=" + SPRING_BOOT_H2_PASSWORD,
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
    "idpValidateInterval=2",
    "perry.tokenRecordTimeout=240"
}, classes = {PerryLoginTestConfiguration.class, PerryApplication.class})

public class PerryMFALoginTest extends BaseIntegrationTest {

  public static final String SECURED_RESOURCE_URL = "/authn/login?callback=/demo-sp.html";
  public static final String MFA_LOGIN_URL = "http://localhost/mfa-login.html";
  public static final String LOGIN_REDIRECT_URL = "http://localhost/authn/login?callback=/demo-sp.html";
  public static final String AUTHN_TOKEN_URL = "/authn/token?accessCode=";
  public static final String AUTHN_VALIDATE_URL = "/authn/validate?token=";
  public static final String AUTHN_LOGOUT_URL = "/authn/logout?callback=/login.html";
  public static final String ERROR_PAGE_URL = "/error";
  public static final String LOGOUT_REDIRECT_URL = "/login.html";
  public static final String VALID_MFA_RESPONSE_JSON = "fixtures/mfa/mfa-response.json";
  public static final String MISSING_RACFID_MFA_RESPONSE_JSON = "fixtures/mfa/mfa-response-missing-racfid.json";
  public static final String KEY_INFO_MISSING_MFA_RESPONSE_JSON = "fixtures/mfa/mfa-response-key-info-missing.json";
  public static final String AUTH_JSON = "fixtures/mfa/auth.json";
  public static final String AUTH_MISSING_INFO_JSON = "fixtures/mfa/auth-missing-info.json";
  public static final String AUTH_NO_RACFID_JSON = "fixtures/mfa/auth-no-racfid.json";
  public static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
  public static final String USERNAME = "9a11585d-0a86-4715-bedf-3cf783bc4baf";

  private static final Logger LOGGER = LoggerFactory.getLogger(PerryMFALoginTest.class);

  @Autowired
  private OAuth2Service oAuth2Service;

  @MockBean
  private NsUserService nsUserService;

  @BeforeClass
  public static void beforeClass() throws Exception {
    runLiquibaseScript(CMS_STORE_URL, "liquibase/cms-data.xml");
  }

  @Test
  public void whenValidMFAJsonProvided_thenAuthenticate() throws Exception {
    NsUser nsUser = nsUserWithoutKeyInfo();
    nsUser.setRacfid("BRADYG");
    nsUser.setRoles(toSet("CWS-worker", "County-admin"));
    nsUser.setPermissions(toSet("Snapshot-rollout", "Facility-search-rollout"));
    when(nsUserService.findByUsername(USERNAME)).thenReturn(Optional.of(nsUser));

    MvcResult result = navigateToSecureUrl();
    result = sendMfaJson(result, FixtureHelpers.fixture(VALID_MFA_RESPONSE_JSON),
        LOGIN_REDIRECT_URL);
    result = retrieveAccessCode(result);
    String accessCode = parseAccessCode(result);
    result = retrieveToken(result, accessCode);
    String token = result.getResponse().getContentAsString();
    LOGGER.info("Perry token: {}", token);
    Assert.assertTrue(token.matches(UUID_PATTERN));
    result = validateToken(token, MockMvcResultMatchers.status().isOk());
    String perryJson = result.getResponse().getContentAsString();
    LOGGER.info("Perry JSON: {}", perryJson);
    JSONAssert.assertEquals(FixtureHelpers.fixture(AUTH_JSON), perryJson, false);
    setLogoutHandlerSecurityContext(token, result.getRequest().getSession());
    logout(result);
    validateToken(token, MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  public void whenEmptyMFAJsonProvided_thenPerryErrorPage() {
    when(nsUserService.findByUsername(any())).thenReturn(Optional.empty());

    try {
      MvcResult result = navigateToSecureUrl();
      sendMfaJson(result, "{}", ERROR_PAGE_URL);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof PerryException);
      Assert.assertTrue(e.getMessage().startsWith("COGNITO RESPONSE PROCESSING ERROR"));
    }
  }

  @Test
  public void whenRacfidMissingMFAJsonProvided_thenLoginSuccessfully() throws Exception {
    NsUser nsUser = nsUserWithoutKeyInfo();
    nsUser.setRoles(toSet("CWS-worker", "County-admin"));
    nsUser.setPermissions(toSet("Snapshot-rollout", "Facility-search-rollout"));
    when(nsUserService.findByUsername(USERNAME)).thenReturn(Optional.of(nsUser));

    Mockito.when(oAuth2Service.getUserInfo(EXPECTED_SSO_TOKEN))
        .thenReturn(MockOAuth2Service.constructUserInfo(MISSING_RACFID_MFA_RESPONSE_JSON));
    runLoginFlow(MISSING_RACFID_MFA_RESPONSE_JSON, AUTH_NO_RACFID_JSON);
  }

  @Test
  public void whenKeyInfoMissingMFAJsonProvided_thenLoginSuccessfully() throws Exception {
    NsUser nsUser = nsUserWithoutKeyInfo();
    when(nsUserService.findByUsername(USERNAME)).thenReturn(Optional.of(nsUser));

    Mockito.when(oAuth2Service.getUserInfo(EXPECTED_SSO_TOKEN))
        .thenReturn(MockOAuth2Service.constructUserInfo(KEY_INFO_MISSING_MFA_RESPONSE_JSON));
    runLoginFlow(KEY_INFO_MISSING_MFA_RESPONSE_JSON, AUTH_MISSING_INFO_JSON);
  }

  @Test
  public void whenInvalidMFAJsonProvided_thenPerryErrorPage() {
    when(nsUserService.findByUsername(any())).thenReturn(Optional.empty());

    try {
      MvcResult result = navigateToSecureUrl();
      sendMfaJson(result, "Invalid JSON", ERROR_PAGE_URL);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof PerryException);
      Assert.assertTrue(e.getMessage().startsWith("COGNITO RESPONSE PROCESSING ERROR"));
    }
  }

  private static NsUser nsUserWithoutKeyInfo() {
    NsUser nsUser = new NsUser();
    nsUser.setUsername(USERNAME);
    nsUser.setFirstName("Greg");
    nsUser.setLastName("Brady");
    nsUser.setPhoneNumber("19161111111");
    return nsUser;
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
    result = validateToken(token, MockMvcResultMatchers.status().isOk());
    String perryJson = result.getResponse().getContentAsString();
    LOGGER.info("Perry JSON: {}", perryJson);
    JSONAssert.assertEquals(FixtureHelpers.fixture(authJson), perryJson, false);
    return result;
  }

  private void setLogoutHandlerSecurityContext(String token, HttpSession session) {
    UniversalUserToken userToken = new UniversalUserToken();
    userToken.setToken(token);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userToken,
            "",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    SecurityContext securityContext = new SecurityContextImpl();
    securityContext.setAuthentication(authentication);
    session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        securityContext);
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

  private MvcResult validateToken(String token, ResultMatcher resultMatcher) throws Exception {
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
