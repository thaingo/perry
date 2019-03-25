package gov.ca.cwds.idm;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.idm.service.notification.NotificationTypes.USER_LOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.ABSENT_USER_ID;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.NEW_USER_SUCCESS_ID;
import static gov.ca.cwds.idm.util.TestUtils.asJsonString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.ca.cwds.idm.dto.IdmNotification;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.UserLockedEvent;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
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

    IdmNotification notification = new IdmNotification(NEW_USER_SUCCESS_ID, USER_LOCKED);
    long oldUserLogsSize = userLogRepository.count();

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .contentType(JSON_CONTENT_TYPE)
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isAccepted())
        .andReturn();

    verify(auditEventService, times(1)).processAuditEvent(any(
        UserLockedEvent.class));

    verify(spyUserIndexService, times(1)).updateUserInIndex(argThat(User::isLocked));

    assertThat(userLogRepository.count(), is(oldUserLogsSize));
  }

  @Test
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void testNotifyUserWasLockedDoraError() throws Exception {

    IdmNotification notification = new IdmNotification(NEW_USER_SUCCESS_ID, USER_LOCKED);
    long oldUserLogsSize = userLogRepository.count();

    setDoraError();

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .contentType(JSON_CONTENT_TYPE)
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isAccepted())
        .andReturn();

    verify(auditEventService, times(1)).processAuditEvent(any(
        UserLockedEvent.class));

    assertThat(userLogRepository.count(), is(oldUserLogsSize + 1));
  }

  @Test
  public void testNotifyNoAuthHeaderUnauthorized() throws Exception {

    IdmNotification notification = new IdmNotification("userId", USER_LOCKED);

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .contentType(JSON_CONTENT_TYPE)
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  public void testNotifyInvalidCredsUnauthorized() throws Exception {

    IdmNotification notification = new IdmNotification("userId", USER_LOCKED);

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

    IdmNotification notification = new IdmNotification(ABSENT_USER_ID, USER_LOCKED);

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

    IdmNotification notification = new IdmNotification(NEW_USER_SUCCESS_ID, "invalidOperation");

    mockMvc
        .perform(MockMvcRequestBuilders.post("/idm/notifications/")
            .contentType(JSON_CONTENT_TYPE)
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .content(asJsonString(notification)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();
  }

  private static String prepareNotValidBasicAuthHeader() {
    String authString = "invalidUser" + ":" + "InvalidPass";
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
    String authStringEnc = new String(authEncBytes);
    return "Basic " + authStringEnc;
  }
}
