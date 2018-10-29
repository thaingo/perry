package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.INACTIVE_USER_WITH_ACTIVE_RACFID_IN_CMS;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.INACTIVE_USER_WITH_NO_ACTIVE_RACFID_IN_CMS;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.INACTIVE_USER_WITH_NO_RACFID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.STATE_ADMIN_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_NO_RACFID_ID;
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
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.google.common.collect.Iterables;
import gov.ca.cwds.config.LoggingRequestIdFilter;
import gov.ca.cwds.config.LoggingUserIdFilter;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class IdmResourceTest2 extends IdmResourceTest {

  @Test
  @WithMockCustomUser
  public void testUpdateUser() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setPermissions(toSet("RFA-rollout", "Hotline-rollout"));
    userUpdate.setRoles(toSet("Office-admin", "CWS-worker"));

    AdminUpdateUserAttributesRequest updateAttributesRequest =
        setUpdateUserAttributesRequestAndResult(
            USER_NO_RACFID_ID,
            attr(PERMISSIONS.getName(), "RFA-rollout:Hotline-rollout"),
            attr(ROLES.getName(), "Office-admin:CWS-worker")
        );

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
  public void testUpdateUserNotAllowedRole() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setRoles(toSet("State-admin"));

    assertUpdateBadRequest(USER_NO_RACFID_ID, userUpdate,
        "fixtures/idm/update-user/not-allowed-role-error.json");
  }

  @Test
  @WithMockCustomUser
  public void testUpdateRemoveAllRoles() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setRoles(toSet());

    assertUpdateBadRequest(USER_NO_RACFID_ID, userUpdate,
        "fixtures/idm/update-user/no-roles-error.json");
  }

  @Test
  @WithMockCustomUser
  public void testUpdateRolesAreNotChanged() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);

    setDoraSuccess();
    setDisableUserRequestAndResult(USER_NO_RACFID_ID);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + USER_NO_RACFID_ID)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testValidationUpdateUserChangeInactiveToActive_throwsNoRacfIdInCWS() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.TRUE);
    userUpdate.setRoles(toSet(CWS_WORKER));

    AdminEnableUserRequest enableUserRequest = setEnableUserRequestAndResult(INACTIVE_USER_WITH_NO_ACTIVE_RACFID_IN_CMS);
    setDoraSuccess();

    assertUpdateBadRequest(INACTIVE_USER_WITH_NO_ACTIVE_RACFID_IN_CMS, userUpdate,
        "fixtures/idm/update-user/no-active-cws-user-error.json");

    verify(spySearchService, times(0)).updateUser(any(User.class));
    verify(cognito, times(0)).adminEnableUser(enableUserRequest);
    verifyDoraCalls(0);
  }

  @Test
  @WithMockCustomUser
  public void testValidationUpdateUserChangeInactiveToActive_withNoRacfIdForUser() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.TRUE);
    userUpdate.setRoles(toSet(CWS_WORKER));

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
    userUpdate.setRoles(toSet(CWS_WORKER));
    AdminEnableUserRequest enableUserRequest = setEnableUserRequestAndResult(INACTIVE_USER_WITH_ACTIVE_RACFID_IN_CMS);
    setDoraSuccess();

    assertUpdateBadRequest(INACTIVE_USER_WITH_ACTIVE_RACFID_IN_CMS, userUpdate,
        "fixtures/idm/update-user/active-user-with-same-racfid-in-cognito-error.json");

    verify(spySearchService, times(0)).updateUser(any(User.class));
    verify(cognito, times(0)).adminEnableUser(enableUserRequest);
    verifyDoraCalls(0);
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
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
  @WithMockCustomUser(roles = {STATE_ADMIN})
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
  public void testUpdateUser_CountyAdminCannotUpdateStateAdmin() throws Exception {
    assertUpdateUserUnauthorized(STATE_ADMIN_ID);
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

  @Test
  @WithMockCustomUser
  public void testVerifyUsers() throws Exception {
    assertVerify("test@test.com", "SMITHB3", "fixtures/idm/verify-user/verify-valid.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyErrorMessageForUserWithActiveStatusInCognito() throws Exception {
    assertVerify("test@test.com", "SMITHBO",
        "fixtures/idm/verify-user/verify-active-racfid-already-in-cognito-message.json");
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserWithActiveStatusInCognito() throws Exception {
    User user = racfIdUser("test@test.com", "SMITHBO", toSet(CWS_WORKER));
    assertCreateUserBadRequest(user,
        "fixtures/idm/create-user/active-user-with-same-racfid-in-cognito-error.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsersRacfidInLowerCase() throws Exception {
    assertVerify("test@test.com", "smithb3", "fixtures/idm/verify-user/verify-valid.json");
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testCreateRacfidUser() throws Exception {
    User user = getElroydaUser();
    User actuallySendUser = getActuallySendElroydaUser();
    ((TestCognitoServiceFacade) cognitoServiceFacade).setSearchByRacfidRequestAndResult("ELROYDA");

    assertCreateUserSuccess(user, actuallySendUser, NEW_USER_SUCCESS_ID_4);
  }

  @Test
  @WithMockCustomUser
  public void testCreateRacfidUserUnautorized() throws Exception {
    User user = getElroydaUser();
    User actuallySendUser = getActuallySendElroydaUser();
    AdminCreateUserRequest request = setCreateRequestAndResult(actuallySendUser, NEW_USER_SUCCESS_ID_4);
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
  public void testVerifyUsersWithEmailInMixedCase() throws Exception {
    assertVerify("Test@Test.com", "SMITHB3", "fixtures/idm/verify-user/verify-valid.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsersNoRacfIdInCws() throws Exception {
    assertVerifyUserNoRacfidInCws();
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserNoRacfIdInCws() throws Exception {
    User user = user("test@test.com");
    user.setRacfid("SMITHB1");
    user.setRoles(toSet(CWS_WORKER));

    assertCreateUserBadRequest(user, "fixtures/idm/create-user/no-racfid-in-cws-error.json");
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN}, county = "Madera")
  public void testVerifyUserStateAdmin() throws Exception {
    assertVerifyUserNoRacfidInCws();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testVerifyUserOfficeAdmin() throws Exception {
    assertVerifyUserNoRacfidInCws();
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"otherOfficeId"})
  public void testVerifyUserOfficeAdminOtherOffice() throws Exception {
    assertVerify("test@test.com", "SMITHB3", "fixtures/idm/verify-user/verify-other-office.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsersCognitoUserIsPresent() throws Exception {
    assertVerify("julio@gmail.com", "SMITHBO", "fixtures/idm/verify-user/verify-user-present.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testVerifyUserWithOtherRole() throws Exception {
    assertVerifyUserUnauthorized();
  }

  @Test
  @WithMockCustomUser(county = "Madera")
  public void testVerifyUsersOtherCounty() throws Exception {
    assertVerify("test@test.com", "SMITHB3", "fixtures/idm/verify-user/verify-other-county.json");
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testVerifyUsersCalsAdmin() throws Exception {
    assertVerifyUserUnauthorized();
  }


  @Test
  @WithMockCustomUser(county = "OtherCounty")
  public void testResendInvitationEmailWithDifferentCounty() throws Exception {
    assertResendEmailUnauthorized(YOLO_COUNTY_USERS_EMAIL);
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"otherOfficeId"})
  public void testResendInvitationEmailWithOfficeRole() throws Exception {
    assertResendEmailUnauthorized(YOLO_COUNTY_USERS_EMAIL);
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testResendInvitationEmailWithOtherRole() throws Exception {
    assertResendEmailUnauthorized(YOLO_COUNTY_USERS_EMAIL);
  }

  private void assertResendEmailUnauthorized(String email) throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/resend?email=" + email))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN}, county = "Madera")
  public void testResendInvitationEmailWithStateAdmin() throws Exception {
    assertResendEmailWorksFine();
  }

  @Test
  @WithMockCustomUser()
  public void testResendInvitationEmailWithCountyAdmin() throws Exception {
    assertResendEmailWorksFine();
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

  private void assertAdminOffices(String fixtureName) throws Exception {
    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/admin-offices"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    assertStrict(result, "fixtures/idm/admin-offices/" + fixtureName);
  }
}
