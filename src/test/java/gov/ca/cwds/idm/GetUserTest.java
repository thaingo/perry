package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.ABSENT_IN_IDM_USER_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.ABSENT_IN_NS_DB_USER_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.COUNTY_ADMIN_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.ERROR_USER_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.LOCKED_USER;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.OFFICE_ADMIN_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.STATE_ADMIN_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.SUPER_ADMIN_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.UNLOCKED_USER;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_NO_RACFID_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_NO_LOCKED_VALUE_UNLOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_NO_PHONE_EXTENSION;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_RACFID_AND_CWS_STAFF_AUTHORITY_PRVILIGES;
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
  public void testGetUserWithRacfIdAndCwsStaffPrivileges() throws Exception {
    testGetValidUser(
        USER_WITH_RACFID_AND_CWS_STAFF_AUTHORITY_PRVILIGES,
        "fixtures/idm/get-user/with-racfid-and-cws-staff-privs.json");
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
  public void testGetAbsentInNsDbUser() throws Exception {

    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/" + ABSENT_IN_NS_DB_USER_ID))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();
    assertExtensible(result, "fixtures/idm/get-user/absent-in-ns-db.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetAbsentInIdmUser() throws Exception {

    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/" + ABSENT_IN_IDM_USER_ID))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();
    assertExtensible(result, "fixtures/idm/get-user/absent-in-idm.json");
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
  @WithMockCustomUser(roles = {SUPER_ADMIN})
  public void testGetSuperAdminBySuperAdmin() throws Exception {
    testGetValidUser(SUPER_ADMIN_ID, "fixtures/idm/get-user/super-admin.json");
  }

  @Test
  @WithMockCustomUser(roles = {SUPER_ADMIN})
  public void testGetStateAdminBySuperAdmin() throws Exception {
    testGetValidUser(STATE_ADMIN_ID, "fixtures/idm/get-user/state-admin-by-super-admin.json");
  }

  //LOCKING FEATURE TESTS:

  @Test
  @WithMockCustomUser
  public void testGetUserShouldBeUnlocked() throws Exception {
    testGetValidUser(UNLOCKED_USER,
        "fixtures/idm/get-user/unlocked_user.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetUserNoLockedValueShouldBeUnlocked() throws Exception {
    testGetValidUser(USER_WITH_NO_LOCKED_VALUE_UNLOCKED,
        "fixtures/idm/get-user/no-locked-value-unlocked.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetUserShouldBeLocked() throws Exception {
    testGetValidUser(LOCKED_USER,
        "fixtures/idm/get-user/locked-user.json");
  }

  @Test
  @WithMockCustomUser(roles = {OFFICE_ADMIN})
  public void testGetOfficeAdminByOfficeAdminInSameOffice() throws Exception {
    testGetValidUser(OFFICE_ADMIN_ID,
        "fixtures/idm/get-user/office-admin-by-office-admin-in-same-office.json");
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
