package gov.ca.cwds.idm;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.idm.dto.NotificationType.USER_LOCKED;
import static gov.ca.cwds.idm.dto.NotificationType.USER_PASSWORD_CHANGED;
import static gov.ca.cwds.idm.dto.NotificationType.USER_REGISTRATION_COMPLETE;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.ABSENT_USER_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.LOCKED_USER;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_NO_RACFID_ID;
import static gov.ca.cwds.idm.util.TestUtils.asJsonString;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.ca.cwds.idm.dto.IdmUserNotification;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.UserLockedEvent;
import gov.ca.cwds.idm.event.UserPasswordChangedEvent;
import gov.ca.cwds.idm.event.UserRegistrationCompleteEvent;
import gov.ca.cwds.idm.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;


public class IdmNotificationTest extends BaseIdmIntegrationWithSearchTest {

  @Before
  public void before() {
    super.before();
    setDoraSuccess();
  }

  @Test
  public void testNotifyUserWasLocked() throws Exception {

    IdmUserNotification notification = new IdmUserNotification(LOCKED_USER, USER_LOCKED);
    long oldUserLogsSize = userLogRepository.count();

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .contentType(JSON_CONTENT_TYPE)
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isAccepted())
        .andReturn();

    verify(auditEventService, times(1)).saveAuditEvent(any(
        UserLockedEvent.class));

    verify(spyUserIndexService, times(1)).updateUserInIndex(argThat(User::isLocked));

    assertThat(userLogRepository.count(), is(oldUserLogsSize));
  }

  @Test
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void testNotifyUserWasLockedDoraError() throws Exception {

    IdmUserNotification notification = new IdmUserNotification(LOCKED_USER, USER_LOCKED);
    long oldUserLogsSize = userLogRepository.count();

    setDoraError();

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .contentType(JSON_CONTENT_TYPE)
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isAccepted())
        .andReturn();

    verify(auditEventService, times(1)).saveAuditEvent(any(
        UserLockedEvent.class));

    assertThat(userLogRepository.count(), is(oldUserLogsSize + 1));
  }

  @Test
  public void testNotifyNoAuthHeaderUnauthorized() throws Exception {

    IdmUserNotification notification = new IdmUserNotification("userId", USER_LOCKED);

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .contentType(JSON_CONTENT_TYPE)
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  public void testNotifyInvalidCredsUnauthorized() throws Exception {

    IdmUserNotification notification = new IdmUserNotification(LOCKED_USER, USER_LOCKED);

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .contentType(JSON_CONTENT_TYPE)
            .header(HttpHeaders.AUTHORIZATION, prepareNotValidBasicAuthHeader())
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  public void testNotifyUserNotFound() throws Exception {

    IdmUserNotification notification = new IdmUserNotification(ABSENT_USER_ID, USER_LOCKED);

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .contentType(JSON_CONTENT_TYPE)
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();
  }

  @Test
  public void testNotifyInvalidOperation() throws Exception {

    String invalidNotification = fixture("fixtures/idm/notify/invalid.json");

    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .contentType(JSON_CONTENT_TYPE)
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .content(invalidNotification))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    assertExtensible(result, "fixtures/idm/notify/invalid-result.json");
  }

  @Test
  public void testNotifyUserChangedPassword() throws Exception {

    IdmUserNotification notification =
        new IdmUserNotification(USER_NO_RACFID_ID, USER_PASSWORD_CHANGED);

    long oldUserLogsSize = userLogRepository.count();

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .contentType(JSON_CONTENT_TYPE)
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isAccepted())
        .andReturn();

    verify(auditEventService, times(1)).saveAuditEvent(any(
        UserPasswordChangedEvent.class));

    verify(spyUserIndexService, times(1)).updateUserInIndex(
        argThat(user -> user.getId().equals(USER_NO_RACFID_ID)));

    assertThat(userLogRepository.count(), is(oldUserLogsSize));
  }

  @Test
  public void testNotifyRegistrationComplete() throws Exception {
    IdmUserNotification notification =
        new IdmUserNotification(USER_NO_RACFID_ID, USER_REGISTRATION_COMPLETE);

    long oldUserLogsSize = userLogRepository.count();

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .contentType(JSON_CONTENT_TYPE)
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isAccepted())
        .andReturn();

    verify(auditEventService, times(1)).saveAuditEvent(any(
        UserRegistrationCompleteEvent.class));

    verify(spyUserIndexService, times(1)).updateUserInIndex(
        argThat(user -> user.getId().equals(USER_NO_RACFID_ID)));

    assertThat(userLogRepository.count(), is(oldUserLogsSize));

  }

  private static String prepareNotValidBasicAuthHeader() {
    return TestUtils.prepareBasicAuthHeader("invalidUser", "InvalidPass");
  }
}
