package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_NO_RACFID_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_RACFID_AND_DB_DATA_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_RACFID_ID;
import static gov.ca.cwds.idm.persistence.ns.OperationType.CREATE;
import static gov.ca.cwds.idm.persistence.ns.OperationType.UPDATE;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.google.common.collect.Iterables;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import java.time.LocalDateTime;
import java.util.Iterator;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class SecondIdmResourceTest extends IdmResourceTest {

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
  public void testCreateUserNoRacfIdInCws() throws Exception {
    User user = user("test@test.com");
    user.setRacfid("SMITHB1");
    user.setRoles(toSet(CWS_WORKER));

    assertCreateUserBadRequest(user, "fixtures/idm/create-user/no-racfid-in-cws-error.json");
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
}
