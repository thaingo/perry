package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_RACFID_ID;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.util.WithMockCustomUser;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class ResendInvitationEmailIdmResourceTest extends BaseIdmResourceTest {

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

  private void assertResendEmailUnauthorized(String email) throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/resend?email=" + email))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  private void assertResendEmailWorksFine() throws Exception {
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
}
