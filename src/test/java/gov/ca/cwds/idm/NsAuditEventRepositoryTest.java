package gov.ca.cwds.idm;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

public class NsAuditEventRepositoryTest extends BaseIdmIntegrationTest {

  private static final String ID1 = "id1";
  private static final String ID2 = "id2";
  private static final String ID3 = "id3";

  private static final String AUDIT_EVENT = "{\"\"audit_event\": 1}";

  @Test
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void testSaveLoad() {

    NsAuditEvent auditEvent1 = new NsAuditEvent();
    auditEvent1.setId(ID1);
    auditEvent1.setAuditEvent(AUDIT_EVENT);
    auditEvent1.setProcessed(true);
    auditEvent1.setEventTimestamp(LocalDateTime.now());
    nsAuditEventRepository.save(auditEvent1);

    NsAuditEvent auditEvent2 = new NsAuditEvent();
    auditEvent2.setId(ID2);
    auditEvent2.setAuditEvent(AUDIT_EVENT);
    auditEvent2.setEventTimestamp(LocalDateTime.now());

    NsAuditEvent auditEvent3 = new NsAuditEvent();
    auditEvent3.setId(ID3);
    auditEvent3.setAuditEvent(AUDIT_EVENT);
    auditEvent3.setEventTimestamp(LocalDateTime.now());

    nsAuditEventRepository.save(Arrays.asList(auditEvent2, auditEvent3));

    assertEquals(3, nsAuditEventRepository.count());
    assertEquals(AUDIT_EVENT, nsAuditEventRepository.findOne(ID1).getAuditEvent());
    assertEquals(AUDIT_EVENT, nsAuditEventRepository.findOne(ID3).getAuditEvent());
    assertEquals(ID2, nsAuditEventRepository.findOne(ID2).getId());
    assertTrue(nsAuditEventRepository.findOne(ID1).isProcessed());
    assertFalse(nsAuditEventRepository.findOne(ID2).isProcessed());
  }
}
