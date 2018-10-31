package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.IdmResource.DATETIME_FORMAT_PATTERN;
import static gov.ca.cwds.idm.IdmResourceTest.DORA_WS_MAX_ATTEMPTS;
import static gov.ca.cwds.idm.IdmResourceTest.IDM_BASIC_AUTH_PASS;
import static gov.ca.cwds.idm.IdmResourceTest.IDM_BASIC_AUTH_USER;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USERPOOL;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_RACFID_ID;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertStrict;
import static gov.ca.cwds.util.LiquibaseUtils.CMS_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.TOKEN_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.runLiquibaseScript;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserResult;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gov.ca.cwds.BaseIntegrationTest;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import gov.ca.cwds.idm.persistence.ns.repository.UserLogRepository;
import gov.ca.cwds.idm.service.IdmServiceImpl;
import gov.ca.cwds.idm.service.SearchRestSender;
import gov.ca.cwds.idm.service.SearchService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.cognito.SearchProperties;
import gov.ca.cwds.service.messages.MessagesService;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@ActiveProfiles({"dev", "idm"})
@SpringBootTest(
    properties = {
        "perry.identityManager.idmBasicAuthUser=" + IDM_BASIC_AUTH_USER,
        "perry.identityManager.idmBasicAuthPass=" + IDM_BASIC_AUTH_PASS,
        "perry.identityManager.idmMapping=config/idm.groovy",
        "spring.jpa.hibernate.ddl-auto=none",
        "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
        "spring.datasource.url=" + CMS_STORE_URL,
        "perry.doraWsMaxAttempts=" + DORA_WS_MAX_ATTEMPTS,
        "perry.doraWsRetryDelayMs=500"
    }
)
public abstract class IdmResourceTest extends BaseIntegrationTest {

  protected static final String NEW_USER_SUCCESS_ID_2 = "17067e4e-270f-4623-b86c-b4d4fa527a35";
  protected static final String NEW_USER_SUCCESS_ID_3 = "17067e4e-270f-4623-b86c-b4d4fa527a36";
  protected static final String NEW_USER_SUCCESS_ID_4 = "17067e4e-270f-4623-b86c-b4d4fa527a37";
  protected static final String SSO_TOKEN = "b02aa833-f8b2-4d28-8796-3abe059313d1";
  protected static final String YOLO_COUNTY_USERS_EMAIL = "julio@gmail.com";
  protected static final String BASIC_AUTH_HEADER = prepareBasicAuthHeader();
  protected static final MediaType JSON_CONTENT_TYPE =
      new MediaType(
          MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(),
          Charset.forName("utf8"));
  protected static final int DORA_WS_MAX_ATTEMPTS = 3;

  @Autowired
  protected CognitoServiceFacade cognitoServiceFacade;

  @Autowired
  protected MessagesService messagesService;

  @Autowired
  protected IdmServiceImpl idmService;

  @Autowired
  protected UserLogRepository userLogRepository;

  @Autowired
  protected SearchService searchService;

  @Autowired
  protected SearchRestSender searchRestSender;

  @Autowired
  protected SearchProperties searchProperties;

  protected SearchService spySearchService;

  protected RestTemplate mockRestTemplate = mock(RestTemplate.class);

  protected AWSCognitoIdentityProvider cognito;

  protected Appender mockAppender = mock(Appender.class);

  @Captor
  protected ArgumentCaptor<LoggingEvent> captorLoggingEvent;

  @BeforeClass
  public static void beforeClass() throws Exception {
    runLiquibaseScript(CMS_STORE_URL, "liquibase/cms-data.xml");
    runLiquibaseScript(TOKEN_STORE_URL, "liquibase/ns-data.xml");
  }

  @Before
  public void before() {

    ((TestCognitoServiceFacade) cognitoServiceFacade).setMessagesService(messagesService);

    searchRestSender.setRestTemplate(mockRestTemplate);
    searchService.setRestSender(searchRestSender);
    searchService.setSearchProperties(searchProperties);
    spySearchService = spy(searchService);

    idmService.setSearchService(spySearchService);
    cognito = ((TestCognitoServiceFacade) cognitoServiceFacade).getIdentityProvider();

    Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.addAppender(mockAppender);
  }

  private static String prepareBasicAuthHeader() {
    String authString = IDM_BASIC_AUTH_USER + ":" + IDM_BASIC_AUTH_PASS;
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
    String authStringEnc = new String(authEncBytes);
    return "Basic " + authStringEnc;
  }

  protected final void assertCreateUserBadRequest(User user, String fixturePath) throws Exception {
    AdminCreateUserRequest request = cognitoServiceFacade.createAdminCreateUserRequest(user);

    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/idm/users")
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(user)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    assertExtensible(result, fixturePath);
    verify(cognito, times(0)).adminCreateUser(request);
  }


  protected final void assertResendEmailWorksFine() throws Exception {
    AdminCreateUserRequest request =
        ((TestCognitoServiceFacade) cognitoServiceFacade)
            .createResendEmailRequest(YOLO_COUNTY_USERS_EMAIL);

    UserType user = new UserType();
    user.setUsername(USER_WITH_RACFID_ID);
    user.setEnabled(true);
    user.setUserStatus("FORCE_CHANGE_PASSWORD");

    AdminCreateUserResult result = new AdminCreateUserResult().withUser(user);
    when(cognito.adminCreateUser(request)).thenReturn(result);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                "/idm/users/resend?email="+YOLO_COUNTY_USERS_EMAIL))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
   }

  protected final void assertAllAdminOffices() throws Exception {
    assertAdminOffices("all-offices.json");
  }

  protected final void assertCountyAdminOffices() throws Exception {
    assertAdminOffices("county-offices.json");
  }

  private void assertAdminOffices(String fixtureName) throws Exception {
    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/admin-offices"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    assertStrict(result, "fixtures/idm/admin-offices/" + fixtureName);
  }

  protected final UserLog userLog(String userName, OperationType operation, LocalDateTime dateTime) {
    UserLog log = new UserLog();
    log.setUsername(userName);
    log.setOperationType(operation);
    log.setOperationTime(dateTime);
    return userLogRepository.save(log);
  }

  protected final void assertUserLog(
      UserLog userLog, String username, OperationType operationType, LocalDateTime time) {
    assertThat(userLog.getUsername(), is(username));
    assertThat(userLog.getOperationType(), is(operationType));
    assertThat(userLog.getOperationTime(), is(time));
  }

  protected final void assertUserLog(
      Iterator<UserLog> iterator, String username, OperationType operationType,
      LocalDateTime time) {
    UserLog userLog = iterator.next();
    assertUserLog(userLog, username, operationType, time);
  }

  protected final static String getDateString(LocalDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN);
    return date.format(formatter);
  }

  protected final void testGetValidUser(String userId, String fixtureFilePath) throws Exception {

    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/users/" + userId))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, fixtureFilePath);
  }

  protected final void testCreateUserValidationError(User user) throws Exception {

    AdminCreateUserRequest request = cognitoServiceFacade.createAdminCreateUserRequest(user);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/idm/users")
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(user)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    verify(cognito, times(0)).adminCreateUser(request);
    verify(spySearchService, times(0)).createUser(any(User.class));
  }

  protected final AdminUpdateUserAttributesRequest setUpdateUserAttributesRequestAndResult(
      String id, AttributeType... userAttributes) {
    AdminUpdateUserAttributesRequest request =
        new AdminUpdateUserAttributesRequest()
            .withUsername(id)
            .withUserPoolId(USERPOOL)
            .withUserAttributes(userAttributes);
    AdminUpdateUserAttributesResult result = new AdminUpdateUserAttributesResult();
    when(cognito.adminUpdateUserAttributes(request)).thenReturn(result);
    return request;
  }

  protected final AdminDisableUserRequest setDisableUserRequestAndResult(String id) {
    AdminDisableUserRequest request =
        new AdminDisableUserRequest().withUsername(id).withUserPoolId(USERPOOL);
    AdminDisableUserResult result = new AdminDisableUserResult();
    when(cognito.adminDisableUser(request)).thenReturn(result);
    return request;
  }

  protected final AdminDisableUserRequest setDisableUserRequestAndFail(String id) {
    AdminDisableUserRequest request =
        new AdminDisableUserRequest().withUsername(id).withUserPoolId(USERPOOL);
    when(cognito.adminDisableUser(request))
        .thenThrow(new RuntimeException("Update enable status error"));
    return request;
  }

  protected final AdminEnableUserRequest setEnableUserRequestAndResult(String id) {
    AdminEnableUserRequest request =
        new AdminEnableUserRequest().withUsername(id).withUserPoolId(USERPOOL);
    AdminEnableUserResult result = new AdminEnableUserResult();
    when(cognito.adminEnableUser(request)).thenReturn(result);
    return request;
  }

  protected final static User user() {
    return user("gonzales@gmail.com");
  }

  protected final User racfIdUser(String email, String racfId, Set<String> roles) {
    User user = new User();
    user.setEmail(email);
    user.setRacfid(racfId);
    user.setRoles(roles);
    return user;
  }

  protected final static User user(String email) {
    User user = new User();
    user.setEmail(email);
    user.setFirstName("Garcia");
    user.setLastName("Gonzales");
    user.setCountyName(WithMockCustomUser.COUNTY);
    user.setOfficeId(WithMockCustomUser.OFFICE_ID);
    return user;
  }

  protected final static String asJsonString(final Object obj) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JavaTimeModule javaTimeModule = new JavaTimeModule();
      objectMapper.registerModule(javaTimeModule);
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Component
  static class TestPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException {
      if (beanName.equals("cognitoServiceFacade")) {
        return new TestCognitoServiceFacade();
      } else if (beanName.equals("searchService")) {
        return new TestSearchService();
      } else {
        return bean;
      }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
      return bean;
    }
  }

  public static class TestSearchService extends SearchService {

    @Override
    protected String getSsoToken() {
      return SSO_TOKEN;
    }
  }

  protected final void setDoraSuccess() {
    when(mockRestTemplate.exchange(
        any(String.class),
        any(HttpMethod.class),
        any(HttpEntity.class),
        any(Class.class),
        any(Map.class)))
        .thenReturn(ResponseEntity.ok().body("{\"success\":true}"));
  }

  protected final void setDoraError() {
    doThrow(new RestClientException("Elastic Search error"))
        .when(mockRestTemplate).exchange(any(String.class), any(HttpMethod.class),
        any(HttpEntity.class), any(Class.class), any(Map.class));
  }

  protected final void verifyDoraCalls(int times) {
    verify(mockRestTemplate, times(times)).exchange(
        any(String.class),
        any(HttpMethod.class),
        any(HttpEntity.class),
        any(Class.class),
        any(Map.class));
  }
}
