package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.idm.event.AuditEvent;
import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import gov.ca.cwds.idm.persistence.ns.repository.NsAuditEventRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "auditEventService")
@Profile("idm")
public class AuditEventService {

  @Autowired
  private NsAuditEventRepository nsAuditEventRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AuditEventIndexService auditEventIndexService;

  @Transactional(TOKEN_TRANSACTION_MANAGER)
  public <T extends AuditEvent> void saveAuditEventsToDb(
      T auditEvent) {
    nsAuditEventRepository.save(mapToNsAuditEvent(auditEvent));
  }

  @Transactional(TOKEN_TRANSACTION_MANAGER)
  public void saveAuditEventsToDb(
      List<? extends AuditEvent> auditEvents) {
    List<NsAuditEvent> nsAuditEvents = auditEvents.stream().map(this::mapToNsAuditEvent)
        .collect(Collectors.toList());
    nsAuditEventRepository.save(nsAuditEvents);
  }

  public <T extends AuditEvent> void sendAuditEventToEsIndex(T event) {
    auditEventIndexService.sendAuditEventToEsIndex(event);
  }

  private <T extends AuditEvent> NsAuditEvent mapToNsAuditEvent(T auditEvent) {
    try {
      NsAuditEvent nsAuditEvent = new NsAuditEvent();
      nsAuditEvent.setId(auditEvent.getId());
      nsAuditEvent.setEventTimestamp(auditEvent.getTimestamp());
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
