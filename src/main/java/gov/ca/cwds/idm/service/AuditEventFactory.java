package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.AuditEvent;
import gov.ca.cwds.idm.event.EmailChangedEvent;
import gov.ca.cwds.idm.event.NotesChangedEvent;
import gov.ca.cwds.idm.event.PermissionsChangedEvent;
import gov.ca.cwds.idm.event.UserCreatedEvent;
import gov.ca.cwds.idm.event.UserEnabledStatusChangedEvent;
import gov.ca.cwds.idm.event.UserRegistrationResentEvent;
import gov.ca.cwds.idm.event.UserRoleChangedEvent;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class AuditEventFactory {

  private DictionaryProvider dictionaryProvider;

  public AuditEvent createAuditUserCreateEvent(User user) {
    return new UserCreatedEvent(user);
  }

  public AuditEvent createUserRegistrationResentEvent(User user) {
    return new UserRegistrationResentEvent(user);
  }

  public AuditEvent createUserEnableStatusUpdateEvent(User existedUser, BooleanDiff enabledDiff) {
    return new UserEnabledStatusChangedEvent(existedUser, enabledDiff);
  }

  public AuditEvent createEmailChangedEvent(User existedUser, StringDiff emailDiff) {
    return new EmailChangedEvent(existedUser, emailDiff);
  }

  public AuditEvent createUpdatePermissionsEvent(User existedUser, StringSetDiff permissionsDiff) {
    List<Permission> permissions = dictionaryProvider.getPermissions();
    return new PermissionsChangedEvent(existedUser, permissionsDiff, permissions);
  }

  public AuditEvent createUserRoleChangedEvent(User existedUser, StringSetDiff rolesDiff) {
    return new UserRoleChangedEvent(existedUser, rolesDiff);
  }

  public AuditEvent createUpdateNotesEvent(User existedUser, StringDiff notesDiff) {
    return new NotesChangedEvent(existedUser, notesDiff);
  }

  @Autowired
  public void setDictionaryProvider(DictionaryProvider dictionaryProvider) {
    this.dictionaryProvider = dictionaryProvider;
  }
}
