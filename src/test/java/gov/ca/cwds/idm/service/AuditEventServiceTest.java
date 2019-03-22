package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import gov.ca.cwds.config.SpringAsyncConfiguration;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.AuditEvent;
import gov.ca.cwds.idm.event.UserLockedEvent;
import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import gov.ca.cwds.idm.persistence.ns.repository.NsAuditEventRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest(
    excludeAutoConfiguration = {FlywayAutoConfiguration.class, LiquibaseAutoConfiguration.class, SpringAsyncConfiguration.class}
)
@ActiveProfiles("dev, idm, asynctest")
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

  @TestConfiguration
  static class ContextConfiguration {
    @Bean(name = "auditLogTaskExecutor")
    @Primary
    public Executor executor() {
      return new SyncTaskExecutor();
    }
  }


  @Test
  public void testSaveAuditEvent() {
    int sizeBefore = Iterables.size(nsAuditEventRepository.findAll());

    AuditEvent event = new UserLockedEvent(mockUser());

    service.processAuditEvent(event);

    assertEquals(1, Iterables.size(nsAuditEventRepository.findAll()) - sizeBefore);

    NsAuditEvent nsAuditEvent = nsAuditEventRepository.findOne(event.getId());
    assertNotNull(nsAuditEvent);
    assertTrue(nsAuditEvent.isProcessed());
  }

  @Test
  public void testSaveAuditEventDoraFail() {

    int sizeBefore = Iterables.size(nsAuditEventRepository.findAll());

    doThrow(new RuntimeException())
        .when(auditEventIndexService)
        .sendAuditEventToEsIndex(any(AuditEvent.class));

    AuditEvent event = new UserLockedEvent(mockUser());
    service.processAuditEvent(event);

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


