package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.idm.event.AuditEvent;
import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import gov.ca.cwds.idm.persistence.ns.repository.NsAuditEventRepository;
import java.util.List;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "auditEventService")
@Profile("idm")
public class AuditEventService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuditEventService.class);

  @Autowired
  private NsAuditEventRepository nsAuditEventRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AuditEventIndexService auditEventIndexService;

  @Transactional(TOKEN_TRANSACTION_MANAGER)
  @Async("auditLogTaskExecutor")
  public <T extends AuditEvent> void processAuditEvent(T auditEvent) {
    NsAuditEvent nsAuditEvent = mapToNsAuditEvent(auditEvent);
    nsAuditEvent = nsAuditEventRepository.save(nsAuditEvent);
    try {
      auditEventIndexService.sendAuditEventToEsIndex(auditEvent);
    } catch (Exception e) {
      nsAuditEvent.setProcessed(false);
      LOGGER.warn("AuditEvent {} has been marked for further processing by the job", nsAuditEvent.getId(), e);
    }
  }

  @Transactional(TOKEN_TRANSACTION_MANAGER)
  @Async("auditLogTaskExecutor")
  public void saveAuditEvents(List<? extends AuditEvent> auditEvents) {
    auditEvents.forEach(this::processAuditEvent);
  }

  @Transactional(TOKEN_TRANSACTION_MANAGER)
  @Async("auditLogTaskExecutor")
  public <T extends AuditEvent> void persistAuditEvent(T auditEvent) {
    NsAuditEvent event = mapToNsAuditEvent(auditEvent);
    event.setProcessed(false);
    nsAuditEventRepository.save(event);
  }

  private <T extends AuditEvent> NsAuditEvent mapToNsAuditEvent(T auditEvent) {
    try {
      NsAuditEvent nsAuditEvent = new NsAuditEvent();
      nsAuditEvent.setId(auditEvent.getId());
      nsAuditEvent.setEventTimestamp(auditEvent.getTimestamp());
      nsAuditEvent.setProcessed(true);
      nsAuditEvent.setAuditEvent(objectMapper.writeValueAsString(auditEvent));
      return nsAuditEvent;
    } catch (JsonProcessingException e) {
      throw new PersistenceException("Can't transform event to string", e);
    }
  }

  public void setNsAuditEventRepository(
      NsAuditEventRepository nsAuditEventRepository) {
    this.nsAuditEventRepository = nsAuditEventRepository;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public void setAuditEventIndexService(
      AuditEventIndexService auditEventIndexService) {
    this.auditEventIndexService = auditEventIndexService;
  }

}
