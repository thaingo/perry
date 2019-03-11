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
import org.springframework.scheduling.annotation.Async;
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
  @Async("auditLogTaskExecutor")
  public <T extends AuditEvent> void saveAuditEvent(
      T auditEvent) {
    nsAuditEventRepository.save(mapToNsAuditEvent(auditEvent));
    sendAuditEventToEsIndex(auditEvent);
  }

  @Transactional(TOKEN_TRANSACTION_MANAGER)
  @Async("auditLogTaskExecutor")
  public void saveAuditEvents(
      List<? extends AuditEvent> auditEvents) {
    List<NsAuditEvent> nsAuditEvents = auditEvents.stream().map(this::mapToNsAuditEvent)
        .collect(Collectors.toList());
    nsAuditEventRepository.save(nsAuditEvents);
    sendAuditEventsToEsIndex(auditEvents);
  }

  @Transactional(TOKEN_TRANSACTION_MANAGER)
  @Async("auditLogTaskExecutor")
  public <T extends AuditEvent> void persistAuditEvent(T auditEvent) {
    NsAuditEvent event = mapToNsAuditEvent(auditEvent);
    event.setProcessed(false);
    nsAuditEventRepository.save(event);
  }

  private <T extends AuditEvent> void sendAuditEventToEsIndex(T event) {
    auditEventIndexService.sendAuditEventToEsIndex(event);
  }

  private <T extends AuditEvent> void sendAuditEventsToEsIndex(List<T> events) {
    events.forEach(auditEventIndexService::sendAuditEventToEsIndex);
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
