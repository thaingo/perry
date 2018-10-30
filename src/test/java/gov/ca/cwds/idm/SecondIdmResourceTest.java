package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.util.Utils.toSet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import gov.ca.cwds.idm.dto.User;
import org.junit.Test;
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
}
