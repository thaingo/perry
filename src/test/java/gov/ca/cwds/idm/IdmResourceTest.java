package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.IdmResource.DATETIME_FORMAT_PATTERN;
import static gov.ca.cwds.idm.IdmResourceTest.DORA_WS_MAX_ATTEMPTS;
import static gov.ca.cwds.idm.IdmResourceTest.IDM_BASIC_AUTH_PASS;
import static gov.ca.cwds.idm.IdmResourceTest.IDM_BASIC_AUTH_USER;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.ABSENT_USER_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.COUNTY_ADMIN_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.ERROR_USER_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.ES_ERROR_CREATE_USER_EMAIL;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.INACTIVE_USER_WITH_ACTIVE_RACFID_IN_CMS;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.INACTIVE_USER_WITH_NO_ACTIVE_RACFID_IN_CMS;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.INACTIVE_USER_WITH_NO_RACFID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.NEW_USER_ES_FAIL_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.NEW_USER_SUCCESS_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.SOME_PAGINATION_TOKEN;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.STATE_ADMIN_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USERPOOL;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_CALS_EXTERNAL;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_NO_RACFID_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_NO_PHONE_EXTENSION;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_RACFID_AND_DB_DATA_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_RACFID_ID;
import static gov.ca.cwds.idm.persistence.ns.OperationType.CREATE;
import static gov.ca.cwds.idm.persistence.ns.OperationType.UPDATE;
import static gov.ca.cwds.idm.service.cognito.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertStrict;
import static gov.ca.cwds.idm.util.TestUtils.attr;
import static gov.ca.cwds.util.LiquibaseUtils.CMS_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.TOKEN_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.runLiquibaseScript;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

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
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import gov.ca.cwds.BaseIntegrationTest;
import gov.ca.cwds.config.LoggingRequestIdFilter;
import gov.ca.cwds.config.LoggingUserIdFilter;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
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
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
public class IdmResourceTest extends BaseIntegrationTest {

  private static final String NEW_USER_SUCCESS_ID_2 = "17067e4e-270f-4623-b86c-b4d4fa527a35";
  private static final String NEW_USER_SUCCESS_ID_3 = "17067e4e-270f-4623-b86c-b4d4fa527a36";
  private static final String SSO_TOKEN = "b02aa833-f8b2-4d28-8796-3abe059313d1";
  private static final String BASIC_AUTH_HEADER = prepareBasicAuthHeader();
  private static final MediaType JSON_CONTENT_TYPE =
      new MediaType(
          MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(),
          Charset.forName("utf8"));
  static final int DORA_WS_MAX_ATTEMPTS = 3;

  @Autowired
  private CognitoServiceFacade cognitoServiceFacade;

  @Autowired
  private MessagesService messagesService;

  @Autowired
  private IdmServiceImpl idmService;

  @Autowired
  private UserLogRepository userLogRepository;

  @Autowired
  private SearchService searchService;

  @Autowired
  private SearchRestSender searchRestSender;

  @Autowired
  private SearchProperties searchProperties;

  private SearchService spySearchService;

  private RestTemplate mockRestTemplate = mock(RestTemplate.class);

  private AWSCognitoIdentityProvider cognito;

  private Appender mockAppender = mock(Appender.class);

  @Captor
  private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

  @BeforeClass
  public static void beforeClass() throws Exception {
    runLiquibaseScript(CMS_STORE_URL, "liquibase/cms-data.xml");
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

  @Test
  @WithMockCustomUser
  public void testGetPermissions() throws Exception {
    assertGetPermissionsSuccess();
  }

  private void assertGetPermissionsSuccess() throws Exception {
    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/permissions"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertStrict(result, "fixtures/idm/permissions/valid.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testGetPermissionsWithOtherRole() throws Exception {
    assertGetPermissionsUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testGetPermissionsStateAdmin() throws Exception {
    assertGetPermissionsSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testGetPermissionsCalsAdmin() throws Exception {
    assertGetPermissionsUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetPermissionsOfficeAdmin() throws Exception {
    assertGetPermissionsSuccess();
  }

  private void assertGetPermissionsUnauthorized() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/permissions"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testGetUserNoRacfId() throws Exception {
    testGetValidUser(USER_NO_RACFID_ID, "fixtures/idm/get-user/no-racfid-valid.json");
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetUserOfficeAdmin() throws Exception {
    testGetValidUser(USER_WITH_RACFID_AND_DB_DATA_ID,
        "fixtures/idm/get-user/with-racfid-and-db-data-valid-3.json");
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"otherOfficeId"})
  public void testGetUserOfficeAdminOtherOffice() throws Exception {
    testGetValidUser(USER_WITH_RACFID_AND_DB_DATA_ID,
        "fixtures/idm/get-user/with-racfid-and-db-data-valid-2.json");
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testGetUserCalsAdminUnauthorized() throws Exception {
    assertGetUserUnauthorized(USER_NO_RACFID_ID);
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testGetUserCalsAdmin() throws Exception {
    testGetValidUser(USER_CALS_EXTERNAL,
        "fixtures/idm/get-user/with-cals-externa-worker-role.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetUserWithRacfId() throws Exception {
    testGetValidUser(USER_WITH_RACFID_ID, "fixtures/idm/get-user/with-racfid-valid-1.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetUserWithRacfIdAndDbData() throws Exception {
    testGetValidUser(
        USER_WITH_RACFID_AND_DB_DATA_ID,
        "fixtures/idm/get-user/with-racfid-and-db-data-valid-1.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetUserWithNoPhoneExtension() throws Exception {
    testGetValidUser(
        USER_WITH_NO_PHONE_EXTENSION,
        "fixtures/idm/get-user/with-racfid-and-no-phone-extension.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetAbsentUser() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/" + ABSENT_USER_ID))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testGetUserError() throws Exception {
    assertGetUserUnauthorized(ERROR_USER_ID);
  }

  @Test
  @WithMockCustomUser(county = "Madera")
  public void testGetUserByOtherCountyAdmin() throws Exception {
    assertGetUserUnauthorized(USER_NO_RACFID_ID);
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testGetUserWithOtherRole() throws Exception {
    assertGetUserUnauthorized(USER_NO_RACFID_ID);
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"OtherOfficeId"})
  public void testGetOtherOfficeCountyAdminByOfficeAdmin() throws Exception {
    assertGetUserUnauthorized(COUNTY_ADMIN_ID);
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"OtherOfficeId"})
  public void testGetOtherOfficeStateAdminByOfficeAdmin() throws Exception {
    assertGetUserUnauthorized(STATE_ADMIN_ID);
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN}, county = "Madera")
  public void testGetUserStateAdminDifferentCounty() throws Exception {
    testGetValidUser(USER_WITH_RACFID_ID, "fixtures/idm/get-user/with-racfid-valid-2.json");
  }

  private void assertGetUserUnauthorized(String userId) throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/" + userId))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  public void testGetUsers() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users")
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/get-users/all-valid.json");
  }

  @Test
  public void testSearchUsersByRacfid() throws Exception {

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/idm/users/search")
                    .contentType(JSON_CONTENT_TYPE)
                    .content("[\"YOLOD\", \"SMITHBO\"]")
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/users-search/valid.json");
  }

  @Test
  public void testSearchUsersByRacfidFilterOutRepeats() throws Exception {

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/idm/users/search")
                    .contentType(JSON_CONTENT_TYPE)
                    .content("[\"YOLOD\", \"yolod\", \"YOLOD\"]")
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/users-search/yolod.json");
  }

  @Test
  public void testGetUsersPage() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users?paginationToken=" + SOME_PAGINATION_TOKEN)
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/get-users/search-valid.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testGetUsersWithOtherRole() throws Exception {
    assertGetUsersUnauthorized();
  }

  @Test
  @WithMockCustomUser()
  public void testGetUsersCountyAdmin() throws Exception {
    assertGetUsersUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testGetUsersWithStateAdmin() throws Exception {
    assertGetUsersUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testGetUsersWithCalsAdmin() throws Exception {
    assertGetUsersUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetUsersWithOfficeAdmin() throws Exception {
    assertGetUsersUnauthorized();
  }

  private void assertGetUsersUnauthorized() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserSuccess() throws Exception {
    assertCreateUserSuccess("gonzales@gmail.com", NEW_USER_SUCCESS_ID);
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN}, county = "Madera")
  public void testCreateUserStateAdmin() throws Exception {
    assertCreateUserSuccess("gonzales2@gmail.com", NEW_USER_SUCCESS_ID_2);
  }

  private void assertCreateUserSuccess(String email, String newUserId) throws Exception {
    User user = user(email);
    AdminCreateUserRequest request = cognitoServiceFacade.createAdminCreateUserRequest(user);
    setCreateUserResult(request, newUserId);

    setDoraSuccess();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/idm/users")
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(user)))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(header().string("location", "http://localhost/idm/users/" + newUserId))
        .andReturn();

    verify(cognito, times(1)).adminCreateUser(request);
    verify(spySearchService, times(1)).createUser(any(User.class));
    verifyDoraCalls(1);
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testCreateUserOfficeAdmin() throws Exception {
    assertCreateUserSuccess("gonzales3@gmail.com", NEW_USER_SUCCESS_ID_3);
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"otherOfficeId"})
  public void testCreateUserOfficeAdminOtherOffice() throws Exception {
    assertCreateUserUnauthorized();
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserDoraFail() throws Exception {

    int oldUserLogsSize = Iterables.size(userLogRepository.findAll());

    User user = user();
    user.setEmail(ES_ERROR_CREATE_USER_EMAIL);

    setDoraError();

    AdminCreateUserRequest request = cognitoServiceFacade.createAdminCreateUserRequest(user);
    setCreateUserResult(request, NEW_USER_ES_FAIL_ID);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/idm/users")
                    .contentType(JSON_CONTENT_TYPE)
                    .content(asJsonString(user)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(
                header().string("location", "http://localhost/idm/users/" + NEW_USER_ES_FAIL_ID))
            .andReturn();

    assertExtensible(result, "fixtures/idm/partial-success-user-create/log-success.json");

    verify(cognito, times(1)).adminCreateUser(request);
    verify(spySearchService, times(1)).createUser(any(User.class));
    verifyDoraCalls(DORA_WS_MAX_ATTEMPTS);

    Iterable<UserLog> userLogs = userLogRepository.findAll();
    int newUserLogsSize = Iterables.size(userLogs);
    assertThat(newUserLogsSize, is(oldUserLogsSize + 1));

    UserLog lastUserLog = Iterables.getLast(userLogs);
    assertTrue(lastUserLog.getOperationType() == OperationType.CREATE);
    assertThat(lastUserLog.getUsername(), is(NEW_USER_ES_FAIL_ID));
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserAlreadyExists() throws Exception {
    User user = user();
    user.setEmail("some.existing@email");

    AdminCreateUserRequest request = cognitoServiceFacade.createAdminCreateUserRequest(user);
    when(cognito.adminCreateUser(request))
        .thenThrow(new UsernameExistsException("user already exists"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/idm/users")
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(user)))
        .andExpect(MockMvcResultMatchers.status().isConflict())
        .andReturn();

    verify(cognito, times(1)).adminCreateUser(request);
    verify(spySearchService, times(0)).createUser(any(User.class));
  }

  @Test
  @WithMockCustomUser(county = "OtherCounty")
  public void testCreateUserInOtherCounty() throws Exception {
    assertCreateUserUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testCreateUserCalsAdmin() throws Exception {
    assertCreateUserUnauthorized();
  }

  private void assertCreateUserUnauthorized() throws Exception {
    User user = user();
    user.setEmail("unauthorized@gmail.com");

    AdminCreateUserRequest request = cognitoServiceFacade.createAdminCreateUserRequest(user);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/idm/users")
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(user)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();

    verify(cognito, times(0)).adminCreateUser(request);
    verify(spySearchService, times(0)).createUser(any(User.class));
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserWithEmptyEmail() throws Exception {
    User user = user();
    user.setEmail("");
    testCreateUserValidationError(user);
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserWithNullFirstName() throws Exception {
    User user = user();
    user.setFirstName(null);
    testCreateUserValidationError(user);
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserWithBlankLastName() throws Exception {
    User user = user();
    user.setLastName("   ");
    testCreateUserValidationError(user);
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserWithEmptyCountyName() throws Exception {
    User user = user();
    user.setCountyName("");
    testCreateUserValidationError(user);
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserCognitoValidationError() throws Exception {
    User user = user();
    user.setOfficeId("long_string_invalid_id");
    AdminCreateUserRequest request = cognitoServiceFacade.createAdminCreateUserRequest(user);
    when(cognito.adminCreateUser(request))
        .thenThrow(new InvalidParameterException("invalid parameter"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/idm/users")
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(user)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    verify(cognito, times(1)).adminCreateUser(request);
  }

  @Test
  @WithMockCustomUser
  public void testUpdateUser() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setPermissions(toSet("RFA-rollout", "Hotline-rollout"));

    AdminUpdateUserAttributesRequest updateAttributesRequest =
        setUpdateUserAttributesRequestAndResult(
            USER_NO_RACFID_ID, attr(PERMISSIONS.getName(), "RFA-rollout:Hotline-rollout"));

    setDoraSuccess();

    AdminDisableUserRequest disableUserRequest = setDisableUserRequestAndResult(USER_NO_RACFID_ID);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + USER_NO_RACFID_ID)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    verify(cognito, times(1)).adminUpdateUserAttributes(updateAttributesRequest);
    verify(cognito, times(1)).adminDisableUser(disableUserRequest);
    verify(spySearchService, times(1)).updateUser(any(User.class));

    InOrder inOrder = inOrder(cognito);
    inOrder.verify(cognito).adminUpdateUserAttributes(updateAttributesRequest);
    inOrder.verify(cognito).adminDisableUser(disableUserRequest);
    verifyDoraCalls(1);
  }

  @Test
  @WithMockCustomUser
  public void testValidationUpdateUserChangeInactiveToActive_throwsNoRacfIdInCWS() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.TRUE);
    AdminEnableUserRequest enableUserRequest = setEnableUserRequestAndResult(INACTIVE_USER_WITH_NO_ACTIVE_RACFID_IN_CMS);
    setDoraSuccess();

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/idm/users/" + INACTIVE_USER_WITH_NO_ACTIVE_RACFID_IN_CMS)
                    .contentType(JSON_CONTENT_TYPE)
                    .content(asJsonString(userUpdate)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
    assertExtensible(result, "fixtures/idm/update-user/no-active-cws-user-error.json");

    verify(spySearchService, times(0)).updateUser(any(User.class));
    verify(cognito, times(0)).adminEnableUser(enableUserRequest);
    verifyDoraCalls(0);
  }

  @Test
  @WithMockCustomUser
  public void testValidationUpdateUserChangeInactiveToActive_withNoRacfIdForUser() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.TRUE);
    AdminEnableUserRequest enableUserRequest = setEnableUserRequestAndResult(INACTIVE_USER_WITH_NO_RACFID);
    setDoraSuccess();

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/idm/users/" + INACTIVE_USER_WITH_NO_RACFID)
                    .contentType(JSON_CONTENT_TYPE)
                    .content(asJsonString(userUpdate)))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andReturn();

    verify(spySearchService, times(1)).updateUser(any(User.class));
    verify(cognito, times(1)).adminEnableUser(enableUserRequest);
    verifyDoraCalls(1);
  }

  @Test
  @WithMockCustomUser
  public void testValidationUpdateUserChangeInactiveToActive_throwsActiveRacfIdAlreadyInCognito() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.TRUE);
    AdminEnableUserRequest enableUserRequest = setEnableUserRequestAndResult(INACTIVE_USER_WITH_ACTIVE_RACFID_IN_CMS);
    setDoraSuccess();

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/idm/users/" + INACTIVE_USER_WITH_ACTIVE_RACFID_IN_CMS)
                    .contentType(JSON_CONTENT_TYPE)
                    .content(asJsonString(userUpdate)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
    assertExtensible(result, "fixtures/idm/update-user/active-user-with-same-racfid-in-cognito-error.json");

    verify(spySearchService, times(0)).updateUser(any(User.class));
    verify(cognito, times(0)).adminEnableUser(enableUserRequest);
    verifyDoraCalls(0);
  }

  @Test
  @WithMockCustomUser
  public void testUpdateUserDoraFail() throws Exception {

    setDoraError();

    int oldUserLogsSize = Iterables.size(userLogRepository.findAll());

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setPermissions(toSet("RFA-rollout", "Hotline-rollout"));

    AdminUpdateUserAttributesRequest updateAttributesRequest =
        setUpdateUserAttributesRequestAndResult(
            USER_WITH_RACFID_ID, attr(PERMISSIONS.getName(), "RFA-rollout:Hotline-rollout"));

    AdminDisableUserRequest disableUserRequest = setDisableUserRequestAndResult(
        USER_WITH_RACFID_ID);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/idm/users/" + USER_WITH_RACFID_ID)
                    .contentType(JSON_CONTENT_TYPE)
                    .content(asJsonString(userUpdate)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andReturn();

    assertExtensible(result, "fixtures/idm/partial-success-user-update/log-success.json");

    verify(cognito, times(1)).adminUpdateUserAttributes(updateAttributesRequest);
    verify(cognito, times(1)).adminDisableUser(disableUserRequest);
    verify(spySearchService, times(1)).updateUser(any(User.class));
    verifyDoraCalls(DORA_WS_MAX_ATTEMPTS);

    InOrder inOrder = inOrder(cognito);
    inOrder.verify(cognito).adminUpdateUserAttributes(updateAttributesRequest);
    inOrder.verify(cognito).adminDisableUser(disableUserRequest);

    Iterable<UserLog> userLogs = userLogRepository.findAll();
    int newUserLogsSize = Iterables.size(userLogs);
    assertTrue(newUserLogsSize == oldUserLogsSize + 1);

    UserLog lastUserLog = Iterables.getLast(userLogs);
    assertTrue(lastUserLog.getOperationType() == OperationType.UPDATE);
    assertThat(lastUserLog.getUsername(), is(USER_WITH_RACFID_ID));
  }

  @Test
  @WithMockCustomUser
  public void testPartiallySuccessfulUpdate() throws Exception {

    int oldUserLogsSize = Iterables.size(userLogRepository.findAll());

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setPermissions(toSet("RFA-rollout", "Hotline-rollout"));

    AdminUpdateUserAttributesRequest updateAttributesRequest =
        setUpdateUserAttributesRequestAndResult(
            USER_WITH_RACFID_AND_DB_DATA_ID,
            attr(PERMISSIONS.getName(), "RFA-rollout:Hotline-rollout"));

    setDoraSuccess();

    AdminDisableUserRequest disableUserRequest = setDisableUserRequestAndFail(
        USER_WITH_RACFID_AND_DB_DATA_ID);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/idm/users/" + USER_WITH_RACFID_AND_DB_DATA_ID)
                    .contentType(JSON_CONTENT_TYPE)
                    .content(asJsonString(userUpdate)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andReturn();
    assertExtensible(result, "fixtures/idm/partial-success-user-update/partial-update.json");

    verify(cognito, times(1)).adminUpdateUserAttributes(updateAttributesRequest);
    verify(cognito, times(1)).adminDisableUser(disableUserRequest);
    verify(spySearchService, times(1)).updateUser(any(User.class));
    verifyDoraCalls(1);

    InOrder inOrder = inOrder(cognito);
    inOrder.verify(cognito).adminUpdateUserAttributes(updateAttributesRequest);
    inOrder.verify(cognito).adminDisableUser(disableUserRequest);

    Iterable<UserLog> userLogs = userLogRepository.findAll();
    int newUserLogsSize = Iterables.size(userLogs);
    assertTrue(newUserLogsSize == oldUserLogsSize);
  }

  @Test
  @WithMockCustomUser
  public void testIncidentIdisPresentInCustomError() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setPermissions(toSet("RFA-rollout", "Hotline-rollout"));

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/idm/users/" + USER_WITH_RACFID_AND_DB_DATA_ID)
                    .contentType(JSON_CONTENT_TYPE)
                    .content(asJsonString(userUpdate)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andReturn();

    verify(mockAppender, atLeast(1)).doAppend(captorLoggingEvent.capture());
    LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    Map<String, String> mdcMap = loggingEvent.getMDCPropertyMap();
    assertTrue(mdcMap.containsKey(LoggingRequestIdFilter.REQUEST_ID));
    String requestId = mdcMap.get(LoggingRequestIdFilter.REQUEST_ID);
    assertNotNull(requestId);
    assertTrue(mdcMap.containsKey(LoggingUserIdFilter.USER_ID));
    assertThat(mdcMap.get(LoggingUserIdFilter.USER_ID), is("userId"));
    String strResponse = result.getResponse().getContentAsString();
    assertThat(
        strResponse, containsString("\"incident_id\":\"" + requestId + "\""));
  }

  @Test
  @WithMockCustomUser
  public void testUpdateUserNoPermissions() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.TRUE);

    AdminEnableUserRequest enableUserRequest = setEnableUserRequestAndResult(USER_NO_RACFID_ID);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + USER_NO_RACFID_ID)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    verify(cognito, times(0)).adminEnableUser(enableUserRequest);
    verify(spySearchService, times(0)).createUser(any(User.class));
  }

  @Test
  @WithMockCustomUser(county = "Madera")
  public void testUpdateUserByOtherCountyAdmin() throws Exception {
    assertUpdateUserUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testUpdateUserCalsAdmin() throws Exception {
    assertUpdateUserUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testUpdateUserWithOtherRole() throws Exception {
    assertUpdateUserUnauthorized();
  }

  @Test
  @WithMockCustomUser
  public void testUpdateUserNoChanges() throws Exception {
    assertUpdateNoChangesSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN}, county = "Madera")
  public void testUpdateUserStateAdminIsAuthorized() throws Exception {
    assertUpdateNoChangesSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testUpdateUserOfficeAdminIsAuthorized() throws Exception {
    assertUpdateNoChangesSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"otherOfficeId"})
  public void testUpdateUserOfficeOtherOffice() throws Exception {
    assertUpdateUserUnauthorized();
  }

  private void assertUpdateNoChangesSuccess() throws Exception {
    String userId = USER_WITH_RACFID_AND_DB_DATA_ID;
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.TRUE);
    userUpdate.setPermissions(toSet("test"));

    AdminUpdateUserAttributesRequest updateAttributesRequest =
        setUpdateUserAttributesRequestAndResult(
            userId, attr(PERMISSIONS.getName(), "test"));

    AdminEnableUserRequest enableUserRequest = setEnableUserRequestAndResult(userId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + userId)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    verify(cognito, times(0)).adminUpdateUserAttributes(updateAttributesRequest);
    verify(cognito, times(0)).adminEnableUser(enableUserRequest);
    verify(spySearchService, times(0)).createUser(any(User.class));
    verifyDoraCalls(0);
  }

  private void assertUpdateUserUnauthorized() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setPermissions(toSet("RFA-rollout", "Hotline-rollout"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + USER_WITH_RACFID_AND_DB_DATA_ID)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsers() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=SMITHB3"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-valid.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyErrorMessageForUserWithActiveStatusInCognito() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=SMITHBO"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result,
        "fixtures/idm/verify-user/verify-active-racfid-already-in-cognito-message.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsersRacfidInLowerCase() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=smithb3"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-valid.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsersWithEmailInMixedCase() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users/verify?email=Test@Test.com&racfid=SMITHB3"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-valid.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsersNoRacfId() throws Exception {
    assertVerufyUserSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN}, county = "Madera")
  public void testVerifyUserStateAdmin() throws Exception {
    assertVerufyUserSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testVerifyUserOfficeAdmin() throws Exception {
    assertVerufyUserSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"otherOfficeId"})
  public void testVerifyUserOfficeAdminOtherOffice() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=SMITHB3"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-other-office.json");
  }

  private void assertVerufyUserSuccess() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=SMITHB1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-no-racfid.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsersCognitoUserIsPresent() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(
                    "/idm/users/verify?email=julio@gmail.com&racfid=SMITHBO"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-user-present.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testVerifyUserWithOtherRole() throws Exception {
    assertVerifyUserUnauthorized();
  }

  private void assertVerifyUserUnauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=CWDS"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser(county = "Madera")
  public void testVerifyUsersOtherCounty() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=SMITHB3"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-other-county.json");
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testVerifyUsersCalsAdmin() throws Exception {
    assertVerifyUserUnauthorized();
  }

  private void assertResendEmailUnauthorized(AdminCreateUserRequest request) throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/resend/" + USER_WITH_RACFID_ID))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser(county = "OtherCounty")
  public void testResendInvitationEmailWithDifferentCounty() throws Exception {
    AdminCreateUserRequest request =
        ((TestCognitoServiceFacade) cognitoServiceFacade)
            .createResendEmailRequest(USER_WITH_RACFID_ID);
    assertResendEmailUnauthorized(request);
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testResendInvitationEmailWithOtherRole() throws Exception {
    AdminCreateUserRequest request =
        ((TestCognitoServiceFacade) cognitoServiceFacade)
            .createResendEmailRequest(USER_WITH_RACFID_ID);
    assertResendEmailUnauthorized(request);
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"otherOfficeId"})
  public void testResendInvitationEmailWithOfficeRole() throws Exception {
    AdminCreateUserRequest request =
        ((TestCognitoServiceFacade) cognitoServiceFacade)
            .createResendEmailRequest(USER_WITH_RACFID_ID);
    assertResendEmailUnauthorized(request);
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN}, county = "Madera")
  public void testResendInvitationEmailWithStateAdmin() throws Exception {
    AdminCreateUserRequest request =
        ((TestCognitoServiceFacade) cognitoServiceFacade)
            .createResendEmailRequest(USER_WITH_RACFID_ID);

    UserType user = new UserType();
    user.setUsername(USER_WITH_RACFID_ID);
    user.setEnabled(true);
    user.setUserStatus("FORCE_CHANGE_PASSWORD");

    AdminCreateUserResult result = new AdminCreateUserResult().withUser(user);
    when(cognito.adminCreateUser(request)).thenReturn(result);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/resend/" + USER_WITH_RACFID_ID))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    verify(cognito, times(1)).adminCreateUser(request);
  }

  @Test
  public void testGetFailedOperations() throws Exception {
    userLogRepository.deleteAll();
    LocalDateTime log1time = LocalDateTime.of(2018, 1, 1, 12, 0, 15);
    LocalDateTime log0time = log1time.minusHours(4).plusMinutes(13);
    LocalDateTime log2time = log1time.plusMinutes(10);
    LocalDateTime log3time = log2time.plusMinutes(10).minusSeconds(15);
    LocalDateTime log4time = log3time.plusMonths(1).minusHours(7);
    LocalDateTime log5time = log4time.plusWeeks(2).minusHours(6).plusMinutes(18);

    userLog(USER_WITH_RACFID_AND_DB_DATA_ID, CREATE, log0time);
    userLog("this-id-should-be-unused", CREATE, log1time);
    userLog(USER_NO_RACFID_ID, CREATE, log2time);
    userLog(USER_WITH_RACFID_ID, CREATE, log3time);
    userLog(USER_WITH_RACFID_ID, UPDATE, log4time);
    userLog(USER_WITH_RACFID_AND_DB_DATA_ID, UPDATE, log5time);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(
                    "/idm/users/failed-operations?date=" + getDateString(log1time))
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();
    System.out.println(result.getResponse().getContentAsString());

    assertNonStrict(result, "fixtures/idm/failed-operations/failed-operations-valid.json");

    Iterable<UserLog> userLogs = userLogRepository.findAll();
    int newUserLogsSize = Iterables.size(userLogs);
    assertThat(newUserLogsSize, is(4));

    Iterator<UserLog> it = userLogs.iterator();
    assertUserLog(it, USER_NO_RACFID_ID, CREATE, log2time);
    assertUserLog(it, USER_WITH_RACFID_ID, CREATE, log3time);
    assertUserLog(it, USER_WITH_RACFID_ID, UPDATE, log4time);
    assertUserLog(it, USER_WITH_RACFID_AND_DB_DATA_ID, UPDATE, log5time);
  }

  @Test
  @WithMockCustomUser
  public void testGetFailedOperationsNoBasicAuth() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                "/idm/users/failed-operations?date=" + getDateString(LocalDateTime.now())))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  public void testGetFailedOperationsInvalidDateFormat() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users/failed-operations?date=2018-08-01-13.26.33")
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
    assertExtensible(result, "fixtures/idm/failed-operations/failed-operations-invalid-date.json");
  }

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

  private void assertAllAdminOffices() throws Exception {
    assertAdminOffices("all-offices.json");
  }

  private void assertCountyAdminOffices() throws Exception {
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

  private UserLog userLog(String userName, OperationType operation, LocalDateTime dateTime) {
    UserLog log = new UserLog();
    log.setUsername(userName);
    log.setOperationType(operation);
    log.setOperationTime(dateTime);
    return userLogRepository.save(log);
  }

  private void assertUserLog(
      UserLog userLog, String username, OperationType operationType, LocalDateTime time) {
    assertThat(userLog.getUsername(), is(username));
    assertThat(userLog.getOperationType(), is(operationType));
    assertThat(userLog.getOperationTime(), is(time));
  }

  private void assertUserLog(
      Iterator<UserLog> iterator, String username, OperationType operationType,
      LocalDateTime time) {
    UserLog userLog = iterator.next();
    assertUserLog(userLog, username, operationType, time);
  }

  private static String getDateString(LocalDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN);
    return date.format(formatter);
  }

  private void testGetValidUser(String userId, String fixtureFilePath) throws Exception {

    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/users/" + userId))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, fixtureFilePath);
  }

  private void testCreateUserValidationError(User user) throws Exception {

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

  private AdminUpdateUserAttributesRequest setUpdateUserAttributesRequestAndResult(
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

  private AdminDisableUserRequest setDisableUserRequestAndResult(String id) {
    AdminDisableUserRequest request =
        new AdminDisableUserRequest().withUsername(id).withUserPoolId(USERPOOL);
    AdminDisableUserResult result = new AdminDisableUserResult();
    when(cognito.adminDisableUser(request)).thenReturn(result);
    return request;
  }

  private AdminDisableUserRequest setDisableUserRequestAndFail(String id) {
    AdminDisableUserRequest request =
        new AdminDisableUserRequest().withUsername(id).withUserPoolId(USERPOOL);
    when(cognito.adminDisableUser(request))
        .thenThrow(new RuntimeException("Update enable status error"));
    return request;
  }

  private AdminEnableUserRequest setEnableUserRequestAndResult(String id) {
    AdminEnableUserRequest request =
        new AdminEnableUserRequest().withUsername(id).withUserPoolId(USERPOOL);
    AdminEnableUserResult result = new AdminEnableUserResult();
    when(cognito.adminEnableUser(request)).thenReturn(result);
    return request;
  }

  private static User user() {
    return user("gonzales@gmail.com");
  }

  private static User user(String email) {
    User user = new User();
    user.setEmail(email);
    user.setFirstName("Garcia");
    user.setLastName("Gonzales");
    user.setCountyName(WithMockCustomUser.COUNTY);
    user.setOfficeId(WithMockCustomUser.OFFICE_ID);
    return user;
  }

  private AdminCreateUserRequest setCreateUserResult(AdminCreateUserRequest request, String newId) {

    UserType newUser = new UserType();
    newUser.setUsername(newId);
    newUser.setEnabled(true);
    newUser.setUserStatus("FORCE_CHANGE_PASSWORD");
    newUser.withAttributes(request.getUserAttributes());

    AdminCreateUserResult result = new AdminCreateUserResult().withUser(newUser);
    when(cognito.adminCreateUser(request)).thenReturn(result);
    return request;
  }

  private static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
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

  private void setDoraSuccess() {
    when(mockRestTemplate.exchange(
        any(String.class),
        any(HttpMethod.class),
        any(HttpEntity.class),
        any(Class.class),
        any(Map.class)))
        .thenReturn(ResponseEntity.ok().body("{\"success\":true}"));
  }

  private void setDoraError() {
    doThrow(new RestClientException("Elastic Search error"))
        .when(mockRestTemplate).exchange(any(String.class), any(HttpMethod.class),
        any(HttpEntity.class), any(Class.class), any(Map.class));
  }

  private void verifyDoraCalls(int times) {
    verify(mockRestTemplate, times(times)).exchange(
        any(String.class),
        any(HttpMethod.class),
        any(HttpEntity.class),
        any(Class.class),
        any(Map.class));
  }
}
