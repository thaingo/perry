package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;

import org.junit.Test;

public class VerifyUserIdmResourceTest extends IdmResourceTest {

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
}
