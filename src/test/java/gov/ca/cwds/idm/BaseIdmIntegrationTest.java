package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.idm.BaseIdmIntegrationTest.DORA_WS_MAX_ATTEMPTS;
import static gov.ca.cwds.idm.BaseIdmIntegrationTest.IDM_BASIC_AUTH_PASS;
import static gov.ca.cwds.idm.BaseIdmIntegrationTest.IDM_BASIC_AUTH_USER;
import static gov.ca.cwds.idm.util.TestHelper.getTestCognitoProperties;
import static gov.ca.cwds.util.LiquibaseUtils.CMS_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.SPRING_BOOT_H2_PASSWORD;
import static gov.ca.cwds.util.LiquibaseUtils.SPRING_BOOT_H2_USER;
import static gov.ca.cwds.util.LiquibaseUtils.TOKEN_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.runLiquibaseScript;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import gov.ca.cwds.BaseIntegrationTest;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.UserLoggedInEventListener;
import gov.ca.cwds.idm.lifecycle.UserLockService;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.persistence.ns.repository.NsAuditEventRepository;
import gov.ca.cwds.idm.persistence.ns.repository.NsUserRepository;
import gov.ca.cwds.idm.persistence.ns.repository.UserLogRepository;
import gov.ca.cwds.idm.service.AuditEventService;
import gov.ca.cwds.idm.service.IdmServiceImpl;
import gov.ca.cwds.idm.service.NsUserService;
import gov.ca.cwds.idm.service.TransactionalUserService;
import gov.ca.cwds.idm.service.UserLogService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.cognito.util.CognitoRequestHelper;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.search.IndexRestSender;
import gov.ca.cwds.idm.service.search.UserIndexService;
import gov.ca.cwds.idm.service.search.UserSearchService;
import gov.ca.cwds.idm.util.TestCognitoServiceFacade;
import gov.ca.cwds.idm.util.TestUtils;
import gov.ca.cwds.idm.util.WithMockCustomUser;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.util.Utils;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles({"dev", "idm"})
@SpringBootTest(
    properties = {
        "perry.identityManager.idmBasicAuthUser=" + IDM_BASIC_AUTH_USER,
        "perry.identityManager.idmBasicAuthPass=" + IDM_BASIC_AUTH_PASS,
        "perry.identityManager.idmMapping=config/idm.groovy",
        "spring.jpa.hibernate.ddl-auto=none",
        "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
        "spring.datasource.hikari.jdbcUrl=" + CMS_STORE_URL,
        "spring.datasource.hikari.username=" + SPRING_BOOT_H2_USER,
        "spring.datasource.hikari.password=" + SPRING_BOOT_H2_PASSWORD,
        "perry.doraWsMaxAttempts=" + DORA_WS_MAX_ATTEMPTS,
        "perry.doraWsRetryDelayMs=500",
        "search.doraBasicAuthUser=ba_user",
        "search.doraBasicAuthPass=ba_pwd",
        "search.usersIndex.name=users",
        "search.usersIndex.type=user",
        "search.auditIndex.name=auditevents",
        "search.auditIndex.type=auditevent"
    }
)
public abstract class BaseIdmIntegrationTest extends BaseIntegrationTest {

  protected static final String BASIC_AUTH_HEADER = prepareBasicAuthHeader();
  protected static final MediaType JSON_CONTENT_TYPE =
      new MediaType(
          MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(),
          Charset.forName("utf8"));
  protected static final int DORA_WS_MAX_ATTEMPTS = 3;

  @Autowired
  protected CognitoServiceFacade cognitoServiceFacade;

  @Autowired
  protected ExceptionFactory exceptionFactory;

  @Autowired
  protected MessagesService messagesService;

  @Autowired
  protected IdmServiceImpl idmService;

  @Autowired
  protected UserLockService userLockService;

  protected CognitoRequestHelper cognitoRequestHelper;

  @Autowired
  protected UserLogRepository userLogRepository;

  @Autowired
  protected NsUserRepository nsUserRepository;

  @Autowired
  protected TransactionalUserService transactionalUserService;

  @Autowired
  protected UserIndexService userIndexService;

  @Autowired
  protected IndexRestSender indexRestSender;

  @Autowired
  protected UserSearchService userSearchService;

  @Autowired
  protected UserLoggedInEventListener userLoggedInEventListener;

  @Autowired
  protected NsUserService nsUserService;

  @Autowired
  protected UserLogService userLogService;

  @Autowired
  protected NsAuditEventRepository nsAuditEventRepository;

  @MockBean
  protected AuditEventService auditEventService;

  protected NsUserRepository spyNsUserRepository;

  protected AWSCognitoIdentityProvider cognito;

  protected Appender mockAppender = mock(Appender.class);

  @Captor
  protected ArgumentCaptor<LoggingEvent> captorLoggingEvent;

  @BeforeClass
  public static void beforeClass() throws Exception {
    runLiquibaseScript(CMS_STORE_URL, "liquibase/cms-data.xml");
    runLiquibaseScript(TOKEN_STORE_URL, "liquibase/ns-data.xml");
  }

  @Before
  public void before() {

    ((TestCognitoServiceFacade) cognitoServiceFacade).setExceptionFactory(exceptionFactory);

    cognito = ((TestCognitoServiceFacade) cognitoServiceFacade).getIdentityProvider();
    cognitoRequestHelper = new CognitoRequestHelper(getTestCognitoProperties());

    Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.addAppender(mockAppender);

    spyNsUserRepository = spy(nsUserRepository);
  }

  private static String prepareBasicAuthHeader() {
    return TestUtils.prepareBasicAuthHeader(IDM_BASIC_AUTH_USER, IDM_BASIC_AUTH_PASS);
  }

  protected final User user() {
    return user("gonzales@gmail.com");
  }

  protected final User user(String email) {
    User user = new User();
    user.setEmail(email);
    user.setFirstName("Garcia");
    user.setLastName("Gonzales");
    user.setCountyName(WithMockCustomUser.COUNTY);
    user.setOfficeId(WithMockCustomUser.OFFICE_ID);
    user.setRoles(Utils.toSet(CWS_WORKER));
    return user;
  }

  protected final User user(String email, Set<String> roles, Set<String> permissions) {
    User user = user(email);
    user.setRoles(roles);
    user.setPermissions(permissions);
    return user;
  }

  protected final User racfIdUser(String email, String racfId, Set<String> roles) {
    User user = user(email);
    user.setRacfid(racfId);
    user.setRoles(roles);
    return user;
  }

  protected final User racfIdUser(String email, String racfId, Set<String> roles,
      Set<String> permissions) {
    User user = racfIdUser(email, racfId, roles);
    user.setPermissions(permissions);
    return user;
  }

  protected void assertNoNsUserInDb(String userId) {
    List<NsUser> newNsUsers = nsUserRepository.findByUsername(userId);
    assertThat(newNsUsers, empty());
  }

  protected NsUser assertNsUserInDb(String userId) {
    List<NsUser> newNsUsers = nsUserRepository.findByUsername(userId);
    assertThat(newNsUsers.size(), is(1));
    return newNsUsers.get(0);
  }

  @Component
  static class TestPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException {
      if (beanName.equals("cognitoServiceFacade")) {
        return new TestCognitoServiceFacade();
      } else {
        return bean;
      }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
      return bean;
    }
  }
}
