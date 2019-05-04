package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.service.PermissionNames.CANS_PERMISSION_NAME;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.COGNITO_USER_ENABLED_ON_CREATE;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.COGNITO_USER_STATUS_ON_CREATE;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.DB_ERROR_CREATE_USER_EMAIL;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.DELETE_ERROR_CREATE_USER_EMAIL;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.EMAIL_ERROR_CREATE_USER_EMAIL;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.ES_ERROR_CREATE_USER_EMAIL;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.NEW_USER_DB_FAIL_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.NEW_USER_DELETE_FAIL_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.NEW_USER_EMAIL_FAIL_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.NEW_USER_ES_FAIL_ID;
import static gov.ca.cwds.idm.util.TestUtils.asJsonString;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import com.google.common.collect.Iterables;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.UserCreatedEvent;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import gov.ca.cwds.idm.util.TestCognitoServiceFacade;
import gov.ca.cwds.idm.util.WithMockCustomUser;
import java.time.LocalDate;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CreateUserTest extends BaseIdmIntegrationWithSearchTest {

  @Test
  @WithMockCustomUser
  public void testCreateUserSuccess() throws Exception {
    assertCreateUserSuccess(user("gonzales@gmail.com"), "new_user_success_id_1");
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN}, county = "Madera")
  public void testCreateUserStateAdmin() throws Exception {
    assertCreateUserSuccess(user("gonzales2@gmail.com"), "new_user_success_id_2");
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testCreateUserOfficeAdmin() throws Exception {
    assertCreateUserSuccess(user("gonzales3@gmail.com"), "new_user_success_id_3");
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"otherOfficeId"})
  public void testCreateUserOfficeAdminOtherOffice() throws Exception {
    assertCreateUserUnauthorized("fixtures/idm/create-user/office-admin-other-office.json");
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserDoraFail() throws Exception {

    int oldUserLogsSize = Iterables.size(userLogRepository.findAll());

    User user = user();
    user.setEmail(ES_ERROR_CREATE_USER_EMAIL);

    setDoraError();

    CognitoCreateRequests requests = setCreateRequestAndResult(user, NEW_USER_ES_FAIL_ID);
    AdminCreateUserRequest request = requests.createRequest;
    AdminCreateUserRequest invitationRequest = requests.invitationRequest;

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
    verify(cognito, times(1)).adminCreateUser(invitationRequest);
    verify(spyUserIndexService, times(1)).createUserInIndex(any(User.class));
    verifyDoraCalls(DORA_WS_MAX_ATTEMPTS);

    Iterable<UserLog> userLogs = userLogRepository.findAll();
    int newUserLogsSize = Iterables.size(userLogs);
    assertThat(newUserLogsSize, is(oldUserLogsSize + 1));

    UserLog lastUserLog = Iterables.getLast(userLogs);
    assertTrue(lastUserLog.getOperationType() == OperationType.CREATE);
    assertThat(lastUserLog.getUsername(), is(NEW_USER_ES_FAIL_ID));

    assertNsUserInDb(NEW_USER_ES_FAIL_ID);
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserInvitationEmailFail() throws Exception {

    int oldUserLogsSize = Iterables.size(userLogRepository.findAll());

    User user = user();
    user.setEmail(EMAIL_ERROR_CREATE_USER_EMAIL);

    CognitoCreateRequests requests = setCreateRequestAndResultWithEmailError(user,
        NEW_USER_EMAIL_FAIL_ID);
    AdminCreateUserRequest request = requests.createRequest;
    AdminCreateUserRequest invitationRequest = requests.invitationRequest;
    AdminDeleteUserRequest deleteRequest =
        cognitoRequestHelper.getAdminDeleteUserRequest(NEW_USER_EMAIL_FAIL_ID);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/idm/users")
                    .contentType(JSON_CONTENT_TYPE)
                    .content(asJsonString(user)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andReturn();

    assertExtensible(result, "fixtures/idm/create-user/email-fail.json");

    verify(cognito, times(1)).adminCreateUser(request);
    verify(cognito, times(1)).adminCreateUser(invitationRequest);
    verify(cognito, times(1)).adminDeleteUser(deleteRequest);
    verify(spyUserIndexService, times(0)).createUserInIndex(any(User.class));

    Iterable<UserLog> userLogs = userLogRepository.findAll();
    int newUserLogsSize = Iterables.size(userLogs);
    assertThat(newUserLogsSize, is(oldUserLogsSize));

    assertNoNsUserInDb(NEW_USER_EMAIL_FAIL_ID);
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserSavingInDbFail() throws Exception {

    int oldUserLogsSize = Iterables.size(userLogRepository.findAll());

    User user = user();
    user.setEmail(DB_ERROR_CREATE_USER_EMAIL);

    CognitoCreateRequests requests = setCreateRequestAndResult(user, NEW_USER_DB_FAIL_ID);
    AdminCreateUserRequest request = requests.createRequest;
    AdminCreateUserRequest invitationRequest = requests.invitationRequest;
    AdminDeleteUserRequest deleteRequest =
        cognitoRequestHelper.getAdminDeleteUserRequest(NEW_USER_DB_FAIL_ID);

    EntityManager entityManager = transactionalUserService.getEntityManager();
    EntityManager spyEntityManager = spy(transactionalUserService.getEntityManager());
    transactionalUserService.setEntityManager(spyEntityManager);
    doThrow(new RuntimeException("DB error")).when(spyEntityManager).flush();

    MvcResult result;
    try {
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/idm/users")
                  .contentType(JSON_CONTENT_TYPE)
                  .content(asJsonString(user)))
          .andExpect(MockMvcResultMatchers.status().isInternalServerError())
          .andReturn();
    } finally {
      transactionalUserService.setEntityManager(entityManager);
    }

    assertExtensible(result, "fixtures/idm/create-user/db-fail.json");

    verify(cognito, times(1)).adminCreateUser(request);
    verify(cognito, times(0)).adminCreateUser(invitationRequest);
    verify(cognito, times(1)).adminDeleteUser(deleteRequest);
    verify(spyUserIndexService, times(0)).createUserInIndex(any(User.class));

    Iterable<UserLog> userLogs = userLogRepository.findAll();
    int newUserLogsSize = Iterables.size(userLogs);
    assertThat(newUserLogsSize, is(oldUserLogsSize));

    assertNoNsUserInDb(NEW_USER_DB_FAIL_ID);
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserDeleteFail() throws Exception {

    int oldUserLogsSize = Iterables.size(userLogRepository.findAll());

    User user = user();
    user.setEmail(DELETE_ERROR_CREATE_USER_EMAIL);

    CognitoCreateRequests requests = setCreateRequestAndResultWithEmailError(user, NEW_USER_DELETE_FAIL_ID);
    AdminCreateUserRequest request = requests.createRequest;
    AdminCreateUserRequest invitationRequest = requests.invitationRequest;

    AdminDeleteUserRequest deleteRequest =
        cognitoRequestHelper.getAdminDeleteUserRequest(NEW_USER_DELETE_FAIL_ID);
    when(cognito.adminDeleteUser(deleteRequest))
        .thenThrow(new RuntimeException("Cognito delete error"));

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/idm/users")
                    .contentType(JSON_CONTENT_TYPE)
                    .content(asJsonString(user)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andReturn();

    assertExtensible(result, "fixtures/idm/create-user/delete-fail.json");

    verify(cognito, times(1)).adminCreateUser(request);
    verify(cognito, times(1)).adminCreateUser(invitationRequest);
    verify(cognito, times(1)).adminDeleteUser(deleteRequest);
    verify(spyUserIndexService, times(0)).createUserInIndex(any(User.class));

    Iterable<UserLog> userLogs = userLogRepository.findAll();
    int newUserLogsSize = Iterables.size(userLogs);
    assertThat(newUserLogsSize, is(oldUserLogsSize));

    assertNoNsUserInDb(NEW_USER_DELETE_FAIL_ID);
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserAlreadyExists() throws Exception {
    User user = user();
    user.setEmail("some.existing@email");

    AdminCreateUserRequest request = cognitoRequestHelper.getAdminCreateUserRequest(user);
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
    verify(spyUserIndexService, times(0)).createUserInIndex(any(User.class));
  }

  @Test
  @WithMockCustomUser(county = "OtherCounty")
  public void testCreateUserInOtherCounty() throws Exception {
    assertCreateUserUnauthorized("fixtures/idm/create-user/county-admin-other-county.json");
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
    AdminCreateUserRequest request = cognitoRequestHelper.getAdminCreateUserRequest(user);
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
  public void testCreateUserWithActiveStatusInCognito() throws Exception {
    User user = racfIdUser("test@test.com", "SMITHBO", toSet(CWS_WORKER));
    assertCreateUserBadRequest(user,
        "fixtures/idm/create-user/active-user-with-same-racfid-in-cognito-error.json");
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testCreateRacfidUser() throws Exception {
    String email = "Test@Test.com";
    User user = racfidUserNotExistingInCognito(email, toSet(CWS_WORKER), toSet("Hotline-rollout"));
    User actuallySendUser = actuallySendRacfidUserNotExistingInCognito(email.toLowerCase(),
        toSet(CWS_WORKER), toSet("Hotline-rollout"));

    assertCreateUserSuccess(user, actuallySendUser, "new_user_success_id_4");
  }

  @Test
  @WithMockCustomUser
  public void testCreateRacfidUserUnauthorized() throws Exception {
    String email = "Test2@Test.com";
    User user = racfidUserNotExistingInCognito(email, toSet(CWS_WORKER), toSet("Hotline-rollout"));
    User actuallySendUser = actuallySendRacfidUserNotExistingInCognito(email,
        toSet(CWS_WORKER), toSet("Hotline-rollout"));

    CognitoCreateRequests requests = setCreateRequestAndResult(actuallySendUser, "new_user_success_id_5");
    AdminCreateUserRequest request = requests.createRequest;
    AdminCreateUserRequest invitationRequest = requests.invitationRequest;

        MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/idm/users")
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(user)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();

    verify(cognito, times(0)).adminCreateUser(request);
    verify(cognito, times(0)).adminCreateUser(invitationRequest);
    assertExtensible(result, "fixtures/idm/create-user/racfid-user-unauthorized.json");
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserNoRacfIdInCws() throws Exception {
    User user = racfIdUser("test@test.com", "SMITHB1", toSet(CWS_WORKER));
    assertCreateUserBadRequest(user, "fixtures/idm/create-user/no-racfid-in-cws-error.json");
  }

  @Test
  @WithMockCustomUser
  public void testCreateNonRacfidUser_CansPermission() throws Exception {
    User user = user("test@test.com", toSet(CWS_WORKER), toSet(CANS_PERMISSION_NAME));
    assertCreateUserBadRequest(user,
        "fixtures/idm/create-user/no-racfid-user-with-cans-permission.json");
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testCreateRacfidUser_CansPermission() throws Exception {
    String email = "Test3@Test.com";
    User user = racfidUserNotExistingInCognito(email, toSet(CWS_WORKER), toSet(CANS_PERMISSION_NAME));
    User actuallySendUser = actuallySendRacfidUserNotExistingInCognito(email,
        toSet(CWS_WORKER), toSet(CANS_PERMISSION_NAME));

    assertCreateUserSuccess(user, actuallySendUser, "new_cans_racfid_user_success_id");
  }

  @Test
  @WithMockCustomUser
  public void testCreateUser_EmptyRoles() throws Exception {
    User user = user("test@test.com", toSet(), toSet("Snapshot-rollout"));
    assertCreateUserBadRequest(user,
        "fixtures/idm/create-user/user-with-no-roles.json");
  }

  @Test
  @WithMockCustomUser
  public void testCreateUser_NoRoles() throws Exception {
    User user = user("test@test.com", null, toSet("Snapshot-rollout"));
    assertCreateUserBadRequest(user,
        "fixtures/idm/create-user/user-with-no-roles.json");
  }

  @Test
  @WithMockCustomUser
  public void testCreateUser_NotAllowedRole() throws Exception {
    User user = user("test@test.com", toSet(STATE_ADMIN), toSet("Snapshot-rollout"));
    assertCreateUserBadRequest(user,
        "fixtures/idm/create-user/user-with-not-allowed-role.json");
  }

  @Test
  @WithMockCustomUser(roles = {SUPER_ADMIN})
  public void testCreateSuperAdminBySuperAdmin() throws Exception {
    User user = user("super.admin@test.com", toSet(SUPER_ADMIN), toSet("Snapshot-rollout"));
    assertCreateUserSuccess(user, "super_admin_success_id");
  }

  @Test
  @WithMockCustomUser(roles = {SUPER_ADMIN})
  public void testCreateStateAdminBySuperAdmin() throws Exception {
    User user = user("state.admin@test.com", toSet(STATE_ADMIN), toSet("Snapshot-rollout"));
    assertCreateUserSuccess(user, "state_admin_success_id");
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testSuperAdminCannotBeCreatedByStateAdmin() throws Exception {
    User user = user("super.admin@test.com", toSet(SUPER_ADMIN), toSet("Snapshot-rollout"));
    assertCreateUserBadRequest(user,
        "fixtures/idm/create-user/super-admin-by-state-admin.json");
  }

  private void assertCreateUserSuccess(User user, String newUserId) throws Exception {
    assertCreateUserSuccess(user, user, newUserId);
  }

  private void assertCreateUserSuccess(User user, User actuallySendUser, String newUserId) throws Exception {

    assertNoNsUserInDb(newUserId);

    CognitoCreateRequests requests = setCreateRequestAndResult(actuallySendUser, newUserId);
    AdminCreateUserRequest request = requests.createRequest;
    AdminCreateUserRequest invitationRequest = requests.invitationRequest;
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
    verify(cognito, times(1)).adminCreateUser(invitationRequest);
    verify(spyUserIndexService, times(1)).createUserInIndex(argThat(new UserMatcher()));
    verifyDoraCalls(1);
    verify(auditEventService, times(1)).saveAuditEvent(any(
        UserCreatedEvent.class));

    NsUser newNsUser = assertNsUserInDb(newUserId);
    assertThat(newNsUser.getUsername(), is(newUserId));
    assertThat(newNsUser.getRacfid(), is(actuallySendUser.getRacfid()));
    assertThat(newNsUser.getNotes(), is(actuallySendUser.getNotes()));
    assertThat(newNsUser.getPhoneNumber(), is(actuallySendUser.getPhoneNumber()));
    assertThat(newNsUser.getPhoneExtensionNumber(), is(actuallySendUser.getPhoneExtensionNumber()));
    assertThat(newNsUser.getFirstName(), is(actuallySendUser.getFirstName()));
    assertThat(newNsUser.getLastName(), is(actuallySendUser.getLastName()));
    assertThat(newNsUser.getRoles(), equalTo(actuallySendUser.getRoles()));
    assertThat(newNsUser.getPermissions(), equalTo(actuallySendUser.getPermissions()));
  }

  private  CognitoCreateRequests setCreateRequestAndResult(User actuallySendUser,
      String newUserId) {
    TestCognitoServiceFacade testCognitoServiceFacade = (TestCognitoServiceFacade) cognitoServiceFacade;

    AdminCreateUserRequest request = cognitoRequestHelper.getAdminCreateUserRequest(actuallySendUser);
    AdminCreateUserResult result = testCognitoServiceFacade.setCreateUserResult(request, newUserId);
    AdminCreateUserRequest invitationEmailRequest = testCognitoServiceFacade
        .setCreateUserInvitationRequest(actuallySendUser.getEmail(), result);

    return new CognitoCreateRequests(request, invitationEmailRequest);
  }

  private  CognitoCreateRequests setCreateRequestAndResultWithEmailError(User actuallySendUser,
      String newUserId) {
    TestCognitoServiceFacade testCognitoServiceFacade = (TestCognitoServiceFacade) cognitoServiceFacade;

    AdminCreateUserRequest request = cognitoRequestHelper.getAdminCreateUserRequest(actuallySendUser);
    testCognitoServiceFacade.setCreateUserResult(request, newUserId);
    AdminCreateUserRequest invitationEmailRequest = testCognitoServiceFacade
        .setCreateUserInvitationRequestWithEmailError(actuallySendUser.getEmail());
    return new CognitoCreateRequests(request, invitationEmailRequest);
  }

  private MvcResult assertCreateUserUnauthorized() throws Exception {
    User user = user();
    user.setEmail("unauthorized@gmail.com");

    AdminCreateUserRequest request = cognitoRequestHelper.getAdminCreateUserRequest(user);

    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/idm/users")
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(user)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();

    verify(cognito, times(0)).adminCreateUser(request);
    verify(spyUserIndexService, times(0)).createUserInIndex(any(User.class));
    return result;
  }

  private void assertCreateUserUnauthorized(String fixturePath) throws Exception {
    MvcResult result = assertCreateUserUnauthorized();
    assertExtensible(result, fixturePath);
  }

  private User racfidUserNotExistingInCognito(String email, Set<String> roles, Set<String> permissions) {
    return racfIdUser(email, "elroyda", roles, permissions);
  }

  private User actuallySendRacfidUserNotExistingInCognito(String email, Set<String> roles, Set<String> permissions) {
    ((TestCognitoServiceFacade) cognitoServiceFacade).setSearchByRacfidRequestAndResult("ELROYDA");

    User actuallySendUser = racfidUserNotExistingInCognito(email.toLowerCase(), roles, permissions);
    actuallySendUser.setRacfid("ELROYDA");
    actuallySendUser.setFirstName("Donna");
    actuallySendUser.setLastName("Elroy");
    actuallySendUser.setCountyName("Napa");
    actuallySendUser.setOfficeId("TG7O51q0Ki");
    actuallySendUser.setStartDate(LocalDate.of(1998, 4, 14));
    actuallySendUser.setPhoneNumber("4084419876");
    return actuallySendUser;
  }

  private void assertCreateUserBadRequest(User user, String fixturePath) throws Exception {
    AdminCreateUserRequest request = cognitoRequestHelper.getAdminCreateUserRequest(user);

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

  private void testCreateUserValidationError(User user) throws Exception {

    AdminCreateUserRequest request = cognitoRequestHelper.getAdminCreateUserRequest(user);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/idm/users")
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(user)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    verify(cognito, times(0)).adminCreateUser(request);
    verify(spyUserIndexService, times(0)).createUserInIndex(any(User.class));
    verify(auditEventService, never()).saveAuditEvent(any());
  }

  private static final class CognitoCreateRequests {

    private final AdminCreateUserRequest createRequest;
    private final AdminCreateUserRequest invitationRequest;

    CognitoCreateRequests(
        AdminCreateUserRequest createRequest,
        AdminCreateUserRequest invitationRequest) {
      this.createRequest = createRequest;
      this.invitationRequest = invitationRequest;
    }
  }

  private class UserMatcher implements ArgumentMatcher<User> {

    @Override
    public boolean matches(User user) {
      return COGNITO_USER_ENABLED_ON_CREATE.equals(user.getEnabled()) &&
          COGNITO_USER_STATUS_ON_CREATE.equals(user.getStatus());
    }
  }
}
