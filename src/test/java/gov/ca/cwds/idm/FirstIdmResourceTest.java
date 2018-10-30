package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.ABSENT_USER_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.COUNTY_ADMIN_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.ERROR_USER_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.ES_ERROR_CREATE_USER_EMAIL;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.NEW_USER_ES_FAIL_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.NEW_USER_SUCCESS_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.SOME_PAGINATION_TOKEN;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.STATE_ADMIN_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_CALS_EXTERNAL;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_NO_RACFID_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_NO_PHONE_EXTENSION;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_RACFID_AND_DB_DATA_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_RACFID_ID;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;
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
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class FirstIdmResourceTest extends IdmResourceTest {

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
}
