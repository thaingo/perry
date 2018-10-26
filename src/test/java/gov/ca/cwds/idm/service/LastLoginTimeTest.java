package gov.ca.cwds.idm.service;

import static gov.ca.cwds.BaseIntegrationTest.H2_DRIVER_CLASS_NAME;
import static gov.ca.cwds.BaseIntegrationTest.IDM_BASIC_AUTH_PASS;
import static gov.ca.cwds.BaseIntegrationTest.IDM_BASIC_AUTH_USER;
import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.idm.persistence.ns.OperationType.UPDATE;
import static gov.ca.cwds.util.LiquibaseUtils.TOKEN_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.createTokenStoreDatabase;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.USER_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.event.UserLoggedInEvent;
import gov.ca.cwds.idm.dto.UserIdAndOperation;
import gov.ca.cwds.idm.event.UserLoggedInEventListener;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.persistence.ns.repository.UserLogRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@ActiveProfiles({"dev", "idm"})
@SpringBootTest(properties = {
    "perry.identityManager.idmBasicAuthUser=" + IDM_BASIC_AUTH_USER,
    "perry.identityManager.idmBasicAuthPass=" + IDM_BASIC_AUTH_PASS,
    "perry.identityManager.idmMapping=config/idm.groovy",
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
})
public class LastLoginTimeTest {

  private static final String NEW_USER_NAME = "last-login-time-test-unique-username";

  @Autowired
  private UserLoggedInEventListener userLoggedInEventListener;

  @Autowired
  private NsUserService nsUserService;

  @Autowired
  private UserLogService userLogService;

  @Autowired
  private UserLogRepository userLogRepository;

  @BeforeClass
  public static void prepareDatabases() throws Exception {
    Class.forName(H2_DRIVER_CLASS_NAME);
    createTokenStoreDatabase();
  }

  @Test
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void testSaveLastLoginTime() {

    final long FIRST_LOGIN_TIME_MILLIS = 1000000L;
    final long SECOND_LOGIN_TIME_MILLIS = FIRST_LOGIN_TIME_MILLIS + 1000L;

    final LocalDateTime FIRST_LOGIN_TIME = dateTime(FIRST_LOGIN_TIME_MILLIS);
    final LocalDateTime SECOND_LOGIN_TIME = dateTime(SECOND_LOGIN_TIME_MILLIS);

    userLogRepository.deleteAll();

    assertThat(nsUserService.getLastLoginTime(NEW_USER_NAME), is(Optional.empty()));

    userLoggedInEventListener.handleUserLoggedInEvent(loggedInEvent(NEW_USER_NAME, FIRST_LOGIN_TIME));
    assertThat(nsUserService.getLastLoginTime(NEW_USER_NAME), is(Optional.of(FIRST_LOGIN_TIME)));
    assertLastUserLog(dateTime(FIRST_LOGIN_TIME_MILLIS - 100), NEW_USER_NAME, UPDATE);

    userLoggedInEventListener.handleUserLoggedInEvent(loggedInEvent(NEW_USER_NAME, SECOND_LOGIN_TIME));
    assertThat(nsUserService.getLastLoginTime(NEW_USER_NAME), is(Optional.of(SECOND_LOGIN_TIME)));
    assertLastUserLog(dateTime(SECOND_LOGIN_TIME_MILLIS - 100), NEW_USER_NAME, UPDATE);
  }

  private void assertLastUserLog(LocalDateTime startTime, String expectedUserName, OperationType expectedOperation) {
    List<UserIdAndOperation> UserIdAndOperations =
        userLogService.getUserIdAndOperations(startTime);
    assertTrue(UserIdAndOperations.size() > 0);
    UserIdAndOperation lastUserIdAndOperation = UserIdAndOperations.get(UserIdAndOperations.size() - 1);
    assertThat(lastUserIdAndOperation, is(new UserIdAndOperation(expectedUserName, expectedOperation)));
  }

  private LocalDateTime dateTime(long millis) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
  }

  private UserLoggedInEvent loggedInEvent(String username, LocalDateTime loginTime) {
    UniversalUserToken token = new UniversalUserToken();
    token.setParameter(USER_NAME, username);
    return new UserLoggedInEvent(token, loginTime);
  }
}
