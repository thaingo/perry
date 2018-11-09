package gov.ca.cwds.idm;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.idm.persistence.ns.OperationType.UPDATE;
import static gov.ca.cwds.idm.util.TestUtils.dateTime;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.USER_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.event.UserLoggedInEvent;
import gov.ca.cwds.idm.event.UserLoggedInEventListener;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.NsUserService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class LastLoginTimeTest extends BaseIdmIntegrationWithUserLogTest {

  private static final String NEW_USER_NAME = "last-login-time-test-unique-username";

  @Autowired
  private UserLoggedInEventListener userLoggedInEventListener;

  @Autowired
  private NsUserService nsUserService;

  @Test
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void testSaveLastLoginTime() {

    final long FIRST_LOGIN_TIME_MILLIS = 1000000L;
    final long SECOND_LOGIN_TIME_MILLIS = FIRST_LOGIN_TIME_MILLIS + 1000L;

    final LocalDateTime FIRST_LOGIN_TIME = dateTime(FIRST_LOGIN_TIME_MILLIS);
    final LocalDateTime SECOND_LOGIN_TIME = dateTime(SECOND_LOGIN_TIME_MILLIS);

    userLogRepository.deleteAll();

    assertThat(getLastLoginTime(NEW_USER_NAME), nullValue());

    userLoggedInEventListener.handleUserLoggedInEvent(loggedInEvent(NEW_USER_NAME, FIRST_LOGIN_TIME));
    assertThat(getLastLoginTime(NEW_USER_NAME), is(FIRST_LOGIN_TIME));
    assertLastUserLog(dateTime(FIRST_LOGIN_TIME_MILLIS - 100), NEW_USER_NAME, UPDATE);

    userLoggedInEventListener.handleUserLoggedInEvent(loggedInEvent(NEW_USER_NAME, SECOND_LOGIN_TIME));
    assertThat(getLastLoginTime(NEW_USER_NAME), is(SECOND_LOGIN_TIME));
    assertLastUserLog(dateTime(SECOND_LOGIN_TIME_MILLIS - 100), NEW_USER_NAME, UPDATE);
  }

  private UserLoggedInEvent loggedInEvent(String username, LocalDateTime loginTime) {
    UniversalUserToken token = new UniversalUserToken();
    token.setParameter(USER_NAME, username);
    return new UserLoggedInEvent(token, loginTime);
  }

  private LocalDateTime getLastLoginTime(String username) {
    Optional<NsUser> nsUserOpt = nsUserService.findByUsername(username);
    return nsUserOpt.map(NsUser::getLastLoginTime).orElse(null);
  }
}
