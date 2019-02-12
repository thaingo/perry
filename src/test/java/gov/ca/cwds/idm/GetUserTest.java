package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.ABSENT_USER_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.COUNTY_ADMIN_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.ERROR_USER_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.STATE_ADMIN_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.SUPER_ADMIN_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_CALS_EXTERNAL;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_NO_RACFID_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_EXACT_NUMBER_LOGIN_FAILURES_LOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_LESS_THEN_MAX_LOGIN_FAILURES_UNLOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_MORE_THEN_MAX_FAILURES_LOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_NO_LOGIN_FAILURE_UNLOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_NO_PHONE_EXTENSION;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_RACFID_AND_DB_DATA_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_RACFID_AND_INVALID_COUNTY_IN_COGNITO;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_RACFID_ID;

import gov.ca.cwds.idm.util.WithMockCustomUser;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class GetUserTest extends BaseIdmIntegrationTest {

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
    assertGetUserUnauthorized(USER_NO_RACFID_ID,
        "fixtures/idm/get-user/cals-admin-unauthorized.json");
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
  public void testGetUserWithRacfIdAndDbData_InvalidCountyInCognito() throws Exception {
    testGetValidUser(
        USER_WITH_RACFID_AND_INVALID_COUNTY_IN_COGNITO,
        "fixtures/idm/get-user/with-racfid-and-db-data-invalid-county-in-cognito.json");
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
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/" + ERROR_USER_ID))
        .andExpect(MockMvcResultMatchers.status().isInternalServerError())
        .andReturn();
    assertExtensible(result, "fixtures/idm/get-user/internal-error.json");
  }

  @Test
  @WithMockCustomUser(county = "Madera")
  public void testGetUserByOtherCountyAdmin() throws Exception {
    assertGetUserUnauthorized(USER_NO_RACFID_ID,
        "fixtures/idm/get-user/by-other-county-admin.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testGetUserWithOtherRole() throws Exception {
    assertGetUserUnauthorized(USER_NO_RACFID_ID);
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"OtherOfficeId"})
  public void testGetOtherOfficeCountyAdminByOfficeAdmin() throws Exception {
    testGetValidUser(COUNTY_ADMIN_ID, "fixtures/idm/get-user/with-county-admin-id.json");
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN}, adminOfficeIds = {"OtherOfficeId"})
  public void testGetOtherOfficeStateAdminByOfficeAdmin() throws Exception {
    testGetValidUser(STATE_ADMIN_ID, "fixtures/idm/get-user/with-state-admin-id.json");
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN}, county = "Madera")
  public void testGetUserStateAdminDifferentCounty() throws Exception {
    testGetValidUser(USER_WITH_RACFID_ID, "fixtures/idm/get-user/with-racfid-valid-2.json");
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetSuperAdminByOfficeAdmin() throws Exception {
    assertGetUserUnauthorized(SUPER_ADMIN_ID,
        "fixtures/idm/get-user/super-admin-by-office-admin.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetSuperAdminByCountyAdmin() throws Exception {
    assertGetUserUnauthorized(SUPER_ADMIN_ID,
        "fixtures/idm/get-user/super-admin-by-county-admin.json");
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testGetSuperAdminByStateAdmin() throws Exception {
    assertGetUserUnauthorized(SUPER_ADMIN_ID,
        "fixtures/idm/get-user/super-admin-by-state-admin.json");
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testGetSuperAdminByCalsAdmin() throws Exception {
    assertGetUserUnauthorized(SUPER_ADMIN_ID,
        "fixtures/idm/get-user/super-admin-by-cals-admin.json");
  }

  @Test
  @WithMockCustomUser(roles = {SUPER_ADMIN})
  public void testGetSuperAdminBySuperAdmin() throws Exception {
    testGetValidUser(SUPER_ADMIN_ID, "fixtures/idm/get-user/super-admin.json");
  }

  @Test
  @WithMockCustomUser(roles = {SUPER_ADMIN})
  public void testGetStateAdminBySuperAdmin() throws Exception {
    testGetValidUser(STATE_ADMIN_ID, "fixtures/idm/get-user/state-admin-by-super-admin.json");
  }

  //Locking feature tests
  @Test
  @WithMockCustomUser
  public void testGetUserLessThenMaxFailedLoginsShouldBeUnlocked() throws Exception {
    testGetValidUser(USER_WITH_LESS_THEN_MAX_LOGIN_FAILURES_UNLOCKED,
        "fixtures/idm/get-user/less-then-max-failed-logins-unlocked.json");
  }

  //no login failure value available
  @Test
  @WithMockCustomUser
  public void testGetUserNoFailedLoginValueShouldBeUnlocked() throws Exception {
    testGetValidUser(USER_WITH_NO_LOGIN_FAILURE_UNLOCKED,
        "fixtures/idm/get-user/no-failed-login-value-unlocked.json");
  }

  //more then max failed logins to lock
  @Test
  @WithMockCustomUser
  public void testGetUserMoreThanMaxFailedLoginShouldBeLocked() throws Exception {
    testGetValidUser(USER_WITH_MORE_THEN_MAX_FAILURES_LOCKED,
        "fixtures/idm/get-user/more-then-max-failed-logins-locked.json");
  }

  //exact number of failed logins to lock
  @Test
  @WithMockCustomUser
  public void testGetUserExactNumberOfFailedLoginShouldBeLocked() throws Exception {
    testGetValidUser(USER_WITH_EXACT_NUMBER_LOGIN_FAILURES_LOCKED,
        "fixtures/idm/get-user/exact-number-failed-logins-locked.json");
  }

  private MvcResult assertGetUserUnauthorized(String userId) throws Exception {
    return mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/" + userId))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  private void assertGetUserUnauthorized(String userId, String fixturePath) throws Exception {
    MvcResult result = assertGetUserUnauthorized(userId);
    assertExtensible(result, fixturePath);
  }

  private void testGetValidUser(String userId, String fixtureFilePath) throws Exception {

    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/users/" + userId))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, fixtureFilePath);
  }
}
