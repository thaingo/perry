package gov.ca.cwds.idm;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_RACFID_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.RegistrationResubmitResponse;
import gov.ca.cwds.idm.event.UserAuditEvent;
import gov.ca.cwds.idm.util.TestCognitoServiceFacade;
import gov.ca.cwds.idm.util.TestUtils;
import gov.ca.cwds.idm.util.WithMockCustomUser;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional(value = TOKEN_TRANSACTION_MANAGER)
public class ResendInvitationEmailTest extends BaseIdmIntegrationWithUserLogTest {

  private static final String USER_WITH_RACFID_ID_EMAIL = "julio@gmail.com";


  @Test
  @WithMockCustomUser(county = "OtherCounty")
  public void testResendInvitationEmailWithDifferentCounty() throws Exception {
    assertResendEmailUnauthorized(USER_WITH_RACFID_ID,
        "fixtures/idm/resend-invitation-email/with-different-county.json");
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"otherOfficeId"})
  public void testResendInvitationEmailWithOfficeRole() throws Exception {
    assertResendEmailUnauthorized(USER_WITH_RACFID_ID,
        "fixtures/idm/resend-invitation-email/with-office-role.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testResendInvitationEmailWithOtherRole() throws Exception {
    assertResendEmailUnauthorized(USER_WITH_RACFID_ID);
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

  private MvcResult assertResendEmailUnauthorized(String id) throws Exception {
    return mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/users/" + id + "/registration-request"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  private void assertResendEmailUnauthorized(String id, String fixtureFilePath) throws Exception {
    MvcResult result = assertResendEmailUnauthorized(id);
    verify(auditLogService, times(0)).createAuditLogRecord(any(UserAuditEvent.class));
    assertExtensible(result, fixtureFilePath);
  }

  private void assertResendEmailWorksFine() throws Exception {
    AdminCreateUserRequest request =
        ((TestCognitoServiceFacade) cognitoServiceFacade)
            .createResendEmailRequest(USER_WITH_RACFID_ID_EMAIL);

    UserType user = new UserType();
    user.setUsername(USER_WITH_RACFID_ID_EMAIL);
    user.setEnabled(true);
    user.setUserStatus("FORCE_CHANGE_PASSWORD");

    AdminCreateUserResult result = new AdminCreateUserResult().withUser(user);
    when(cognito.adminCreateUser(request)).thenReturn(result);

    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(
                    "/idm/users/" + USER_WITH_RACFID_ID + "/registration-request"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String strResponse = mvcResult.getResponse().getContentAsString();
    RegistrationResubmitResponse registrationResubmitResponse =
        TestUtils.deserialize(strResponse, RegistrationResubmitResponse.class);
    assertThat(registrationResubmitResponse.getUserId(), is(USER_WITH_RACFID_ID));
    verify(auditLogService, times(1)).createAuditLogRecord(any(
        UserAuditEvent.class));
  }

}
