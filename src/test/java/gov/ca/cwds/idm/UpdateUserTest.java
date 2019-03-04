package gov.ca.cwds.idm;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PHONE_EXTENSION;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.PHONE_NUMBER;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.createPermissionsAttribute;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.INACTIVE_USER_WITH_ACTIVE_RACFID_IN_CMS;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.INACTIVE_USER_WITH_NO_ACTIVE_RACFID_IN_CMS;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.INACTIVE_USER_WITH_NO_RACFID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.STATE_ADMIN_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.SUPER_ADMIN_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USERPOOL;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_NO_RACFID_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_RACFID_AND_DB_DATA_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_RACFID_ID;
import static gov.ca.cwds.idm.util.TestUtils.asJsonString;
import static gov.ca.cwds.idm.util.TestUtils.attr;
import static gov.ca.cwds.idm.util.WithMockCustomUserSecurityContextFactory.ADMIN_ID;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserResult;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesResult;
import com.amazonaws.services.cognitoidp.model.AliasExistsException;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.google.common.collect.Iterables;
import gov.ca.cwds.config.LoggingRequestIdFilter;
import gov.ca.cwds.config.LoggingUserIdFilter;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.event.AuditEvent;
import gov.ca.cwds.idm.event.EmailChangedEvent;
import gov.ca.cwds.idm.event.NotesChangedEvent;
import gov.ca.cwds.idm.event.PermissionsChangedEvent;
import gov.ca.cwds.idm.event.UserEnabledStatusChangedEvent;
import gov.ca.cwds.idm.event.UserRoleChangedEvent;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import gov.ca.cwds.idm.util.WithMockCustomUser;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

public class UpdateUserTest extends BaseIdmIntegrationWithSearchTest {

  @Test
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testUpdateUser() throws Exception {

    final String NEW_EMAIL = "newmail@mail.com";
    final String NEW_PHONE = "6889228010";
    final String NEW_PHONE_EXTENSION = "123";
    final String NEW_NOTES = "New notes text";

    NsUser existedNsUser = assertNsUserInDb(USER_NO_RACFID_ID);
    LocalDateTime oldLastModifiedTime = existedNsUser.getLastModifiedTime();

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setEmail(NEW_EMAIL);
    userUpdate.setPhoneNumber(NEW_PHONE);
    userUpdate.setPhoneExtensionNumber(NEW_PHONE_EXTENSION);
    userUpdate.setPermissions(toSet("RFA-rollout", "Hotline-rollout"));
    userUpdate.setRoles(toSet(OFFICE_ADMIN, CWS_WORKER));
    userUpdate.setNotes(NEW_NOTES);

    AdminUpdateUserAttributesRequest updateAttributesRequest =
        setUpdateUserAttributesRequestAndResult(
            USER_NO_RACFID_ID,
            attr(EMAIL, NEW_EMAIL),
            attr(EMAIL_VERIFIED, "True"),
            attr(PHONE_NUMBER, "+" + NEW_PHONE),
            attr(PHONE_EXTENSION, NEW_PHONE_EXTENSION),
            attr(PERMISSIONS, "RFA-rollout:Hotline-rollout"),
            attr(ROLES, "Office-admin:CWS-worker")
        );

    AdminDisableUserRequest disableUserRequest = setDisableUserRequestAndResult(USER_NO_RACFID_ID);

    setDoraSuccess();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + USER_NO_RACFID_ID)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    verify(cognito, times(1)).adminDisableUser(disableUserRequest);
    verify(spySearchService, times(1)).updateUser(any(User.class));

    InOrder inOrder = inOrder(cognito);
    inOrder.verify(cognito).adminUpdateUserAttributes(updateAttributesRequest);
    inOrder.verify(cognito).adminDisableUser(disableUserRequest);

    NsUser updatedNsUser =  assertNsUserInDb(USER_NO_RACFID_ID);
    assertThat(updatedNsUser.getUsername(), is(USER_NO_RACFID_ID));
    assertThat(updatedNsUser.getPhoneNumber(), is(NEW_PHONE));
    assertThat(updatedNsUser.getPhoneExtensionNumber(), is(NEW_PHONE_EXTENSION));
    assertThat(updatedNsUser.getRoles(), is(toSet(OFFICE_ADMIN, CWS_WORKER)));
    assertThat(updatedNsUser.getPermissions(), is(toSet("RFA-rollout", "Hotline-rollout")));
    assertThat(updatedNsUser.getNotes(), is(NEW_NOTES));

//    assertEquals(5, nsAuditEventRepository.count() - previousEventCount);
    LocalDateTime newLastModifiedTime = updatedNsUser.getLastModifiedTime();
    assertThat(newLastModifiedTime, is(notNullValue()));
    assertThat(newLastModifiedTime, is(not(equalTo(oldLastModifiedTime))));

    verifyDoraCalls(1);
    ArgumentCaptor<List<? extends AuditEvent>> captor = ArgumentCaptor.forClass(List.class);
    verify(auditEventService, times(1)).saveAuditEvents(captor.capture());
    List<? extends AuditEvent> events = captor.getValue();
    assertEquals(4, events.size());
    assertTrue(events.stream().anyMatch(e -> e instanceof UserRoleChangedEvent));
    assertTrue(events.stream().anyMatch(e -> e instanceof PermissionsChangedEvent));
    assertTrue(events.stream().anyMatch(e -> e instanceof EmailChangedEvent));
    assertTrue(events.stream().anyMatch(e -> e instanceof NotesChangedEvent));
    verify(auditEventService, times(1))
        .saveAuditEvent(any(UserEnabledStatusChangedEvent.class));
  }

  @Test
  @WithMockCustomUser
  public void testUpdateUserNotAllowedRole() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setRoles(toSet("State-admin"));

    assertUpdateBadRequest(USER_WITH_RACFID_AND_DB_DATA_ID, userUpdate,
        "fixtures/idm/update-user/not-allowed-role-error.json");
  }

  @Test
  @WithMockCustomUser(roles = {STATE_ADMIN})
  public void testUpdatingRolesIsNotAllowed() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.TRUE);
    userUpdate.setRoles(toSet("County-admin"));
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + STATE_ADMIN_ID)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized()).andReturn();
    assertExtensible(result, "fixtures/idm/update-user/update-roles-not-allowed.json");
  }

  @Test
  @WithMockCustomUser
  public void testUpdateRemoveAllRoles() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setRoles(toSet());

    assertUpdateBadRequest(USER_WITH_RACFID_AND_DB_DATA_ID, userUpdate,
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
    verify(auditEventService, never()).saveAuditEvent(any(
        UserRoleChangedEvent.class));
    verify(auditEventService, never()).saveAuditEvent(any(
        EmailChangedEvent.class));
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
            USER_WITH_RACFID_ID, attr(PERMISSIONS, "RFA-rollout:Hotline-rollout"));

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

    NsUser updatedNsUser =  assertNsUserInDb(USER_WITH_RACFID_ID);
    assertThat(updatedNsUser.getPermissions(), equalTo(toSet("RFA-rollout", "Hotline-rollout")));

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
            attr(PERMISSIONS, "RFA-rollout:Hotline-rollout"));

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
    assertExtensible(result,
        "fixtures/idm/partial-success-user-update/partial-update.json");

    verify(cognito, times(1)).adminUpdateUserAttributes(updateAttributesRequest);
    verify(cognito, times(1)).adminDisableUser(disableUserRequest);

    NsUser updatedNsUser =  assertNsUserInDb(USER_WITH_RACFID_AND_DB_DATA_ID);
    assertThat(updatedNsUser.getPermissions(), equalTo(toSet("RFA-rollout", "Hotline-rollout")));

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
    assertThat(mdcMap.get(LoggingUserIdFilter.USER_ID), is(ADMIN_ID));
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
    verify(auditEventService, never()).saveAuditEvent(any(
        PermissionsChangedEvent.class));
    verify(auditEventService, never()).saveAuditEvent(any(
        UserEnabledStatusChangedEvent.class));
  }

  @Test
  @WithMockCustomUser(county = "Madera")
  public void testUpdateUserByOtherCountyAdmin() throws Exception {
    assertUpdateSomeUserUnauthorized(
        "fixtures/idm/update-user/other-county-admin.json");
  }

  @Test
  @WithMockCustomUser(roles = {CALS_ADMIN})
  public void testUpdateUserCalsAdmin() throws Exception {
    assertUpdateSomeUserUnauthorized();
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testUpdateUserWithOtherRole() throws Exception {
    assertUpdateSomeUserUnauthorized();
  }

  @Test
  @WithMockCustomUser
  public void testUpdateUser_CountyAdminCannotUpdateStateAdmin() throws Exception {
    assertUpdateUserUnauthorized(STATE_ADMIN_ID,
        "fixtures/idm/update-user/county-admin-cannot-update-state-admin.json");
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
    assertUpdateSomeUserUnauthorized(
        "fixtures/idm/update-user/other-office.json");
  }

  @Test
  @WithMockCustomUser()
  public void testUpdateEmailOnlyCamelCase() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail("SomeMail@mail.com");

    AdminUpdateUserAttributesRequest updateAttributesRequest =
        setUpdateUserAttributesRequestAndResult(
            USER_NO_RACFID_ID,
            attr(EMAIL, "somemail@mail.com"),
            attr(EMAIL_VERIFIED, "True")
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
    verify(cognito, times(0)).adminDisableUser(disableUserRequest);
    verify(spySearchService, times(1)).updateUser(any(User.class));
    verifyDoraCalls(1);
  }

  @Test
  @WithMockCustomUser()
  public void testUpdateEmailExists() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail("SomeMail@mail.com");

    AdminUpdateUserAttributesRequest request =
        new AdminUpdateUserAttributesRequest()
            .withUsername(USER_WITH_RACFID_ID)
            .withUserPoolId(USERPOOL)
            .withUserAttributes(Arrays.asList(attr(EMAIL, "somemail@mail.com"),
                attr(EMAIL_VERIFIED, "True")));
    when(cognito.adminUpdateUserAttributes(request))
        .thenThrow(new AliasExistsException("the email already used"));

    AdminDisableUserRequest disableUserRequest = setDisableUserRequestAndResult(
        USER_WITH_RACFID_ID);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + USER_WITH_RACFID_ID)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isConflict())
        .andReturn();

    verify(cognito, times(0)).adminDisableUser(disableUserRequest);
    verify(spySearchService, times(0)).updateUser(any(User.class));
    verifyDoraCalls(0);
  }

  @Test
  @WithMockCustomUser
  public void testUpdateNonRacfidUser_CansPermission() throws Exception {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setPermissions(toSet("CANS-rollout"));

    assertUpdateBadRequest(USER_NO_RACFID_ID, userUpdate,
        "fixtures/idm/update-user/non-racfid-user_cans-permission.json");
  }

  @Test
  @WithMockCustomUser
  public void testUpdateRacfidUser_CansPermission() throws Exception {
    assertCanUpdatePermissions(USER_WITH_RACFID_AND_DB_DATA_ID, toSet("CANS-rollout"));
  }

  @Test
  @WithMockCustomUser(roles = {SUPER_ADMIN})
  public void testSuperAdminCanUpdateStateAdminPermissions() throws Exception {
    assertCanUpdatePermissions(STATE_ADMIN_ID, toSet("Hotline-rollout"));
  }

  @Test
  @WithMockCustomUser(roles = {SUPER_ADMIN})
  public void testSuperAdminCanUpdateSuperAdminPermissions() throws Exception {
    assertCanUpdatePermissions(SUPER_ADMIN_ID, toSet("Hotline-rollout"));
  }

  @Test
  @WithMockCustomUser(roles = {SUPER_ADMIN})
  public void testSuperAdminCanDisableSuperAdmin() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);

    assertSuccessfulUpdate(SUPER_ADMIN_ID, userUpdate);
  }

  @Test
  @WithMockCustomUser(roles = {SUPER_ADMIN})
  public void testSuperAdminCanDegradeSuperAdmin() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setRoles(toSet("CWS-worker"));

    assertSuccessfulUpdate(SUPER_ADMIN_ID, userUpdate);
  }

  @Test
  @WithMockCustomUser
  public void testValidationUpdateInvalidPhoneNumber() throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setPhoneNumber("123-456-7890");

    assertUpdateBadRequest(USER_NO_RACFID_ID, userUpdate,
        "fixtures/idm/update-user/invalid-phone-number.json");
  }

  private void assertCanUpdatePermissions(String userId, Set<String> permissions) throws Exception{
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setPermissions(permissions);

    setUpdateUserAttributesRequestAndResult(
        userId,
        createPermissionsAttribute(permissions)
    );

    assertSuccessfulUpdate(userId, userUpdate);
  }

  private void assertSuccessfulUpdate(String userId, UserUpdate userUpdate) throws Exception {
    setDoraSuccess();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + userId)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  private void assertUpdateBadRequest(String userId, UserUpdate userUpdate, String fixture)
      throws Exception {
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + userId)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();
    assertExtensible(result, fixture);
  }

  private void assertUpdateNoChangesSuccess() throws Exception {
    String userId = USER_WITH_RACFID_AND_DB_DATA_ID;
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.TRUE);
    userUpdate.setEmail("garcia@gmail.com");
    userUpdate.setPhoneNumber("4646888777");
    userUpdate.setPhoneExtensionNumber("7");
    userUpdate.setPermissions(toSet("Hotline-rollout"));
    userUpdate.setRoles(toSet("CWS-worker"));

    AdminUpdateUserAttributesRequest updateAttributesRequest =
        setUpdateUserAttributesRequestAndResult(
            userId,
            attr(EMAIL, "garcia@gmail.com"),
            attr(EMAIL_VERIFIED, "True"),
            attr(PHONE_NUMBER, "+4646888777"),
            attr(PHONE_EXTENSION, "7"),
            attr(PERMISSIONS, "Hotline-rollout"),
            attr(ROLES, "CWS-worker")
        );

   AdminEnableUserRequest enableUserRequest = setEnableUserRequestAndResult(userId);
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + userId)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    verify(auditEventService, never()).saveAuditEvent(any());
    verify(cognito, times(0)).adminUpdateUserAttributes(updateAttributesRequest);
    verify(cognito, times(0)).adminEnableUser(enableUserRequest);
    verify(spySearchService, times(0)).createUser(any(User.class));
    verify(spyNsUserRepository, times(0)).save(any(NsUser.class));
    verifyDoraCalls(0);

    NsUser updatedNsUser =  nsUserRepository.findByUsername(USER_WITH_RACFID_AND_DB_DATA_ID).get(0);
    assertThat(updatedNsUser.getNotes(), is(nullValue()));
  }

  private void assertUpdateSomeUserUnauthorized() throws Exception {
    assertUpdateUserUnauthorized(USER_WITH_RACFID_AND_DB_DATA_ID);
  }

  private void assertUpdateSomeUserUnauthorized(String fixturePath) throws Exception {
    assertUpdateUserUnauthorized(USER_WITH_RACFID_AND_DB_DATA_ID, fixturePath);
  }

  private MvcResult assertUpdateUserUnauthorized(String userId) throws Exception {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    userUpdate.setPermissions(toSet("RFA-rollout", "Hotline-rollout"));

    return mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/idm/users/" + userId)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(userUpdate)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  private void assertUpdateUserUnauthorized(String userId, String fixturePath) throws Exception {
    MvcResult result = assertUpdateUserUnauthorized(userId);
    assertExtensible(result, fixturePath);
  }

  private AdminUpdateUserAttributesRequest setUpdateUserAttributesRequestAndResult(
      String id, AttributeType... userAttributes) {
    AdminUpdateUserAttributesRequest request =
        new AdminUpdateUserAttributesRequest()
            .withUsername(id)
            .withUserPoolId(USERPOOL)
            .withUserAttributes(userAttributes);
    AdminUpdateUserAttributesResult result = new AdminUpdateUserAttributesResult();
    when(cognito.adminUpdateUserAttributes(request)).thenReturn(result);
    return request;
  }

  private AdminDisableUserRequest setDisableUserRequestAndResult(String id) {
    AdminDisableUserRequest request =
        new AdminDisableUserRequest().withUsername(id).withUserPoolId(USERPOOL);
    AdminDisableUserResult result = new AdminDisableUserResult();
    when(cognito.adminDisableUser(request)).thenReturn(result);
    return request;
  }

  private AdminDisableUserRequest setDisableUserRequestAndFail(String id) {
    AdminDisableUserRequest request =
        new AdminDisableUserRequest().withUsername(id).withUserPoolId(USERPOOL);
    when(cognito.adminDisableUser(request))
        .thenThrow(new RuntimeException("Update enable status error"));
    return request;
  }

  private AdminEnableUserRequest setEnableUserRequestAndResult(String id) {
    AdminEnableUserRequest request =
        new AdminEnableUserRequest().withUsername(id).withUserPoolId(USERPOOL);
    AdminEnableUserResult result = new AdminEnableUserResult();
    when(cognito.adminEnableUser(request)).thenReturn(result);
    return request;
  }
}
