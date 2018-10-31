package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;

import gov.ca.cwds.idm.util.WithMockCustomUser;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class VerifyUserIdmResourceTest extends BaseIdmResourceTest {

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
  public void testVerifyUsersRacfidInLowerCase() throws Exception {
    assertVerify("test@test.com", "smithb3", "fixtures/idm/verify-user/verify-valid.json");
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

  private void assertVerify(String email, String racfId, String fixturePath) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users/verify?email=" + email + "&racfid=" + racfId))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, fixturePath);
  }

  private void assertVerifyUserNoRacfidInCws() throws Exception {
    assertVerify("test@test.com", "SMITHB1", "fixtures/idm/verify-user/verify-no-racfid.json");
  }

  private void assertVerifyUserUnauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=CWDS"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }
}
