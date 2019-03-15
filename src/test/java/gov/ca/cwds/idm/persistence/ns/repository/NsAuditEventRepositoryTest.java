package gov.ca.cwds.idm.persistence.ns.repository;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest(
    excludeAutoConfiguration = {FlywayAutoConfiguration.class, LiquibaseAutoConfiguration.class}
)
@DirtiesContext
@ActiveProfiles("dev, idm")
public class NsAuditEventRepositoryTest{

  private static final String ID1 = "id1";
  private static final String ID2 = "id2";
  private static final String ID3 = "id3";

  private static final String AUDIT_EVENT = "{\"\"audit_event\": 1}";

  @Autowired
  private NsAuditEventRepository nsAuditEventRepository;

  @MockBean
  private HealthEndpoint healthEndpoint;

  @MockBean
  private InfoEndpoint infoEndpoint;

  @MockBean
  private ObjectMapper objectMapper;

  @Test
  public void testSaveLoad() {
    NsAuditEvent auditEvent1 = new NsAuditEvent();
    auditEvent1.setId(ID1);
    auditEvent1.setAuditEvent(AUDIT_EVENT);
    auditEvent1.setProcessed(true);
    nsAuditEventRepository.save(auditEvent1);
    NsAuditEvent auditEvent2 = new NsAuditEvent();
    auditEvent2.setId(ID2);
    auditEvent2.setAuditEvent(AUDIT_EVENT);
    NsAuditEvent auditEvent3 = new NsAuditEvent();
    auditEvent3.setId(ID3);
    auditEvent3.setAuditEvent(AUDIT_EVENT);
    nsAuditEventRepository.save(Arrays.asList(auditEvent2, auditEvent3));
    assertEquals(3, nsAuditEventRepository.count());
    assertEquals(AUDIT_EVENT, nsAuditEventRepository.findOne(ID1).getAuditEvent());
    assertEquals(AUDIT_EVENT, nsAuditEventRepository.findOne(ID3).getAuditEvent());
    assertEquals(ID2, nsAuditEventRepository.findOne(ID2).getId());
    assertTrue(nsAuditEventRepository.findOne(ID1).isProcessed());
    assertFalse(nsAuditEventRepository.findOne(ID2).isProcessed());
  }

}
