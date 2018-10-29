package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
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
import static gov.ca.cwds.idm.service.cognito.CustomUserAttribute.ROLES;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
public class IdmResourceTest1 extends IdmResourceTest {

//  private static final String NEW_USER_SUCCESS_ID_2 = "17067e4e-270f-4623-b86c-b4d4fa527a35";
//  private static final String NEW_USER_SUCCESS_ID_3 = "17067e4e-270f-4623-b86c-b4d4fa527a36";
//  private static final String NEW_USER_SUCCESS_ID_4 = "17067e4e-270f-4623-b86c-b4d4fa527a37";
//  private static final String SSO_TOKEN = "b02aa833-f8b2-4d28-8796-3abe059313d1";
//  private static final String YOLO_COUNTY_USERS_EMAIL = "julio@gmail.com";
//  private static final String BASIC_AUTH_HEADER = prepareBasicAuthHeader();
//  private static final MediaType JSON_CONTENT_TYPE =
//      new MediaType(
//          MediaType.APPLICATION_JSON.getType(),
//          MediaType.APPLICATION_JSON.getSubtype(),
//          Charset.forName("utf8"));
//  static final int DORA_WS_MAX_ATTEMPTS = 3;
//
//  @Autowired
//  private CognitoServiceFacade cognitoServiceFacade;
//
//  @Autowired
//  private MessagesService messagesService;
//
//  @Autowired
//  private IdmServiceImpl idmService;
//
//  @Autowired
//  private UserLogRepository userLogRepository;
//
//  @Autowired
//  private SearchService searchService;
//
//  @Autowired
//  private SearchRestSender searchRestSender;
//
//  @Autowired
//  private SearchProperties searchProperties;
//
//  private SearchService spySearchService;
//
//  private RestTemplate mockRestTemplate = mock(RestTemplate.class);
//
//  private AWSCognitoIdentityProvider cognito;
//
//  private Appender mockAppender = mock(Appender.class);
//
//  @Captor
//  private ArgumentCaptor<LoggingEvent> captorLoggingEvent;
//
//  @BeforeClass
//  public static void beforeClass() throws Exception {
//    runLiquibaseScript(CMS_STORE_URL, "liquibase/cms-data.xml");
//    runLiquibaseScript(TOKEN_STORE_URL, "liquibase/ns-data.xml");
//  }

//  @Before
//  public void before() {
//
//    ((TestCognitoServiceFacade) cognitoServiceFacade).setMessagesService(messagesService);
//
//    searchRestSender.setRestTemplate(mockRestTemplate);
//    searchService.setRestSender(searchRestSender);
//    searchService.setSearchProperties(searchProperties);
//    spySearchService = spy(searchService);
//
//    idmService.setSearchService(spySearchService);
//    cognito = ((TestCognitoServiceFacade) cognitoServiceFacade).getIdentityProvider();
//
//    Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//    rootLogger.addAppender(mockAppender);
//  }

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
    assertGetPermissionsSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetPermissionsOfficeAdmin() throws Exception {
    assertGetPermissionsSuccess();
  }

  @Test
  @WithMockCustomUser
  public void testGetRoles() throws Exception {
    assertGetRolesSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testGetRolesWithOtherRole() throws Exception {
    assertGetRolesUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testGetRolesStateAdmin() throws Exception {
    assertGetRolesSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testGetRolesCalsAdmin() throws Exception {
    assertGetRolesSuccess();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetRolesOfficeAdmin() throws Exception {
    assertGetRolesSuccess();
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

  @Test
  @WithMockCustomUser
  public void testCreateUserSuccess() throws Exception {
    assertCreateUserSuccess(user("gonzales@gmail.com"), NEW_USER_SUCCESS_ID);
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN}, county = "Madera")
  public void testCreateUserStateAdmin() throws Exception {
    assertCreateUserSuccess(user("gonzales2@gmail.com"), NEW_USER_SUCCESS_ID_2);
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testCreateUserOfficeAdmin() throws Exception {
    assertCreateUserSuccess(user("gonzales3@gmail.com"), NEW_USER_SUCCESS_ID_3);
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

    AdminCreateUserRequest request = setCreateRequestAndResult(user, NEW_USER_ES_FAIL_ID);

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





//  @Component
//  static class TestPostProcessor implements BeanPostProcessor {
//
//    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName)
//        throws BeansException {
//      if (beanName.equals("cognitoServiceFacade")) {
//        return new TestCognitoServiceFacade();
//      } else if (beanName.equals("searchService")) {
//        return new TestSearchService();
//      } else {
//        return bean;
//      }
//    }
//
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName)
//        throws BeansException {
//      return bean;
//    }
//  }

//  public static class TestSearchService extends SearchService {
//
//    @Override
//    protected String getSsoToken() {
//      return SSO_TOKEN;
//    }
//  }
//
//  private void setDoraSuccess() {
//    when(mockRestTemplate.exchange(
//        any(String.class),
//        any(HttpMethod.class),
//        any(HttpEntity.class),
//        any(Class.class),
//        any(Map.class)))
//        .thenReturn(ResponseEntity.ok().body("{\"success\":true}"));
//  }

//  private void setDoraError() {
//    doThrow(new RestClientException("Elastic Search error"))
//        .when(mockRestTemplate).exchange(any(String.class), any(HttpMethod.class),
//        any(HttpEntity.class), any(Class.class), any(Map.class));
//  }
//
//  private void verifyDoraCalls(int times) {
//    verify(mockRestTemplate, times(times)).exchange(
//        any(String.class),
//        any(HttpMethod.class),
//        any(HttpEntity.class),
//        any(Class.class),
//        any(Map.class));
//  }
}
