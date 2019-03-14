package gov.ca.cwds.idm.service;

import static gov.ca.cwds.BaseIntegrationTest.H2_DRIVER_CLASS_NAME;
import static gov.ca.cwds.BaseIntegrationTest.IDM_BASIC_AUTH_PASS;
import static gov.ca.cwds.BaseIntegrationTest.IDM_BASIC_AUTH_USER;
import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.util.LiquibaseUtils.CMS_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.SPRING_BOOT_H2_PASSWORD;
import static gov.ca.cwds.util.LiquibaseUtils.SPRING_BOOT_H2_USER;
import static gov.ca.cwds.util.LiquibaseUtils.TOKEN_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.createCmsDatabase;
import static gov.ca.cwds.util.LiquibaseUtils.createTokenStoreDatabase;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import gov.ca.cwds.PerryApplication;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.AuditEvent;
import gov.ca.cwds.idm.event.UserCreatedEvent;
import gov.ca.cwds.idm.event.UserLockedEvent;
import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import gov.ca.cwds.idm.persistence.ns.repository.NsAuditEventRepository;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.AbstractUriTemplateHandler;

@RunWith(SpringRunner.class)
//@SpringBootTest(properties = {
//    "perry.identityManager.idmBasicAuthUser=" + IDM_BASIC_AUTH_USER,
//    "perry.identityManager.idmBasicAuthPass=" + IDM_BASIC_AUTH_PASS,
//    "perry.identityManager.idmMapping=config/idm.groovy",
//    "spring.jpa.hibernate.ddl-auto=none",
//    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
//    "spring.datasource.hikari.jdbcUrl=" + CMS_STORE_URL,
//    "spring.datasource.hikari.username=" + SPRING_BOOT_H2_USER,
//    "spring.datasource.hikari.password=" + SPRING_BOOT_H2_PASSWORD,
//})
@DataJpaTest(
    excludeAutoConfiguration = {FlywayAutoConfiguration.class, LiquibaseAutoConfiguration.class}
)
@DirtiesContext
@ActiveProfiles("dev, idm")
public class AuditEventServiceTest {

  private static final String TEST_USER_ID = "testId";
  private static final String TEST_FIRST_NAME = "testFirstName";
  private static final String TEST_LAST_NAME = "testLastName";
  private static final String TEST_COUNTY = "testCounty";
  private static final String TEST_OFFICE_ID = "testOfficeId";
  private static final String PERMISSION_1 = "Permission1";
  private static final String PERMISSION_2 = "Permission2";
  private static final String OLD_EMAIL = "oldEmail@gmail.com";

  @MockBean
  private HealthEndpoint healthEndpoint;

  @MockBean
  private InfoEndpoint infoEndpoint;

  @MockBean
  private ObjectMapper objectMapper;


  @Autowired
  private NsAuditEventRepository nsAuditEventRepository;

  @Autowired AuditEventService service;

  @MockBean
  private AuditEventIndexService auditEventIndexService;

  @Configuration
  @Import(PerryApplication.class)
  static class ContextConfiguration {
    @Bean(name = "auditLogTaskExecutor")
    @Primary
    public Executor executor() {
      return new SyncTaskExecutor();
    }
  }

//  @BeforeClass
//  public static void prepareDatabases() throws Exception {
//    Class.forName(H2_DRIVER_CLASS_NAME);
//    createTokenStoreDatabase();
//    createCmsDatabase();
//  }

  @Test
  public void testSaveAuditEvent() {
    int sizeBefore = Iterables.size(nsAuditEventRepository.findAll());

    AuditEvent event = new UserLockedEvent(mockUser());

    service.saveAuditEvent(event);

    assertEquals(1, Iterables.size(nsAuditEventRepository.findAll()) - sizeBefore);

    NsAuditEvent nsAuditEvent = nsAuditEventRepository.findOne(event.getId());
    assertNotNull(nsAuditEvent);
    assertTrue(nsAuditEvent.isProcessed());
  }

  @Test
  public void testSaveAuditEventDoraFail() {

    int sizeBefore = Iterables.size(nsAuditEventRepository.findAll());

    doThrow(new RuntimeException()).when(auditEventIndexService).sendAuditEventToEsIndex(any(AuditEvent.class));

    AuditEvent event = new UserLockedEvent(mockUser());
    service.saveAuditEvent(event);

    assertEquals(1, Iterables.size(nsAuditEventRepository.findAll()) - sizeBefore);

    NsAuditEvent nsAuditEvent = nsAuditEventRepository.findOne(event.getId());

    assertNotNull(nsAuditEvent);
    assertFalse(nsAuditEvent.isProcessed());
  }

  @Test
  public void testPersistEvent() {

    int sizeBefore = Iterables.size(nsAuditEventRepository.findAll());

    AuditEvent event = new UserLockedEvent(mockUser());
    service.persistAuditEvent(event);

    assertEquals(1, Iterables.size(nsAuditEventRepository.findAll()) - sizeBefore);

    NsAuditEvent nsAuditEvent = nsAuditEventRepository.findOne(event.getId());

    assertNotNull(nsAuditEvent);
    assertFalse(nsAuditEvent.isProcessed());
  }

  private User mockUser() {
    User user = new User();
    user.setId(TEST_USER_ID);
    user.setFirstName(TEST_FIRST_NAME);
    user.setLastName(TEST_LAST_NAME);
    user.setCountyName(TEST_COUNTY);
    user.setOfficeId(TEST_OFFICE_ID);
    user.setEmail(OLD_EMAIL);
    user.setRoles(new HashSet<>(Arrays.asList(CWS_WORKER, CALS_ADMIN)));
    user.setPermissions(new TreeSet<>(Arrays.asList(PERMISSION_1, PERMISSION_2)));
    return user;
  }



}
