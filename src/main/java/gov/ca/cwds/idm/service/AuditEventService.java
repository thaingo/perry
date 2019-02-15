package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.AuditEvent;
import gov.ca.cwds.idm.event.EmailChangedEvent;
import gov.ca.cwds.idm.event.NotesChangedEvent;
import gov.ca.cwds.idm.event.PermissionsChangedEvent;
import gov.ca.cwds.idm.event.UserCreatedEvent;
import gov.ca.cwds.idm.event.UserEnabledStatusChangedEvent;
import gov.ca.cwds.idm.event.UserRegistrationResentEvent;
import gov.ca.cwds.idm.event.UserRoleChangedEvent;
import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.persistence.ns.repository.NsAuditEventRepository;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
  private DictionaryProvider dictionaryProvider;

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

  public List<AuditEvent> createUserUpdateEvents(User existedUser,
      UpdateDifference updateDifference) {
    List<AuditEvent> auditEvents = new ArrayList<>(4);
    updateDifference.getRolesDiff().ifPresent(rolesDiff ->
        auditEvents.add(
            enrichAuditEventWithId(new UserRoleChangedEvent(existedUser, rolesDiff))));
    updateDifference.getNotesDiff().ifPresent(notesDiff ->
        auditEvents.add(enrichAuditEventWithId(new NotesChangedEvent(existedUser, notesDiff))));
    updateDifference.getPermissionsDiff().ifPresent(permissionsDiff -> {
          List<Permission> permissions = dictionaryProvider.getPermissions();
          auditEvents.add(enrichAuditEventWithId(
              new PermissionsChangedEvent(existedUser, permissionsDiff, permissions)));
        }
    );
    updateDifference.getEmailDiff().ifPresent(emailDiff ->
        auditEvents.add(enrichAuditEventWithId(new EmailChangedEvent(existedUser, emailDiff)))
    );

    return auditEvents;
  }

  public UserEnabledStatusChangedEvent createUserEnableStatusUpdate(User user,
      BooleanDiff enabledDiff) {
    return enrichAuditEventWithId(new UserEnabledStatusChangedEvent(user, enabledDiff));
  }

  public UserRegistrationResentEvent createResendInvitationEvent(User user) {
    return enrichAuditEventWithId(new UserRegistrationResentEvent(user));
  }

  public UserCreatedEvent createUserCreatedEvent(User user) {
    return enrichAuditEventWithId(new UserCreatedEvent(user));
  }

  private <T extends AuditEvent<S>, S extends Serializable> T enrichAuditEventWithId(
      T auditEvent) {
    auditEvent.setId(UUID.randomUUID().toString());
    return auditEvent;
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

  public void setDictionaryProvider(DictionaryProvider dictionaryProvider) {
    this.dictionaryProvider = dictionaryProvider;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public void setAuditEventIndexService(
      AuditEventIndexService auditEventIndexService) {
    this.auditEventIndexService = auditEventIndexService;
  }

}
