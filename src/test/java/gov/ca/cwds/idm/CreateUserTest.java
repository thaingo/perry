package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.ES_ERROR_CREATE_USER_EMAIL;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.NEW_USER_ES_FAIL_ID;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.TestUtils.asJsonString;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import com.google.common.collect.Iterables;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import gov.ca.cwds.idm.util.WithMockCustomUser;
import java.time.LocalDate;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CreateUserTest extends BaseIdmResourceWithSearchTest {

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
    User user = getElroydaUser();
    User actuallySendUser = getActuallySendElroydaUser();
    ((TestCognitoServiceFacade) cognitoServiceFacade).setSearchByRacfidRequestAndResult("ELROYDA");

    assertCreateUserSuccess(user, actuallySendUser, "new_user_success_id_4");
  }

  @Test
  @WithMockCustomUser
  public void testCreateRacfidUserUnautorized() throws Exception {
    User user = getElroydaUser();
    User actuallySendUser = getActuallySendElroydaUser();
    AdminCreateUserRequest request = setCreateRequestAndResult(actuallySendUser, "new_user_success_id_5");
    ((TestCognitoServiceFacade) cognitoServiceFacade).setSearchByRacfidRequestAndResult("ELROYDA");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/idm/users")
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(user)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();

    verify(cognito, times(0)).adminCreateUser(request);
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserNoRacfIdInCws() throws Exception {
    User user = user("test@test.com");
    user.setRacfid("SMITHB1");
    user.setRoles(toSet(CWS_WORKER));

    assertCreateUserBadRequest(user, "fixtures/idm/create-user/no-racfid-in-cws-error.json");
  }

  private void assertCreateUserSuccess(User user, String newUserId) throws Exception {
    assertCreateUserSuccess(user, user, newUserId);
  }

  private void assertCreateUserSuccess(User user, User actuallySendUser, String newUserId) throws Exception {

    AdminCreateUserRequest request = setCreateRequestAndResult(actuallySendUser, newUserId);
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

  private  AdminCreateUserRequest setCreateRequestAndResult(User actuallySendUser,
      String newUserId) {
    AdminCreateUserRequest request = cognitoServiceFacade.createAdminCreateUserRequest(actuallySendUser);
    ((TestCognitoServiceFacade) cognitoServiceFacade).setCreateUserResult(request, newUserId);
    return request;
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

  private User getElroydaUser() {
    return racfIdUser("Test@Test.com", "elroyda", toSet(CWS_WORKER));
  }

  private User getActuallySendElroydaUser() {
    User actuallySendUser = racfIdUser("test@test.com", "ELROYDA", toSet(CWS_WORKER));
    actuallySendUser.setFirstName("Donna");
    actuallySendUser.setLastName("Elroy");
    actuallySendUser.setCountyName("Napa");
    actuallySendUser.setOfficeId("TG7O51q0Ki");
    actuallySendUser.setStartDate(LocalDate.of(1998, 4, 14));
    return actuallySendUser;
  }

  private void assertCreateUserBadRequest(User user, String fixturePath) throws Exception {
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
}
