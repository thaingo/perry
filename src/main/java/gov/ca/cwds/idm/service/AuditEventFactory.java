package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
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
import gov.ca.cwds.util.IdToNameConverter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class AuditEventFactory {

  private DictionaryProvider dictionaryProvider;

  public UserCreatedEvent createUserCreateEvent(User user) {
    return new UserCreatedEvent(user);
  }

  public UserRegistrationResentEvent createUserRegistrationResentEvent(User user) {
    return new UserRegistrationResentEvent(user);
  }

  public UserEnabledStatusChangedEvent createUserEnableStatusUpdateEvent(User existedUser, BooleanDiff enabledDiff) {
    return new UserEnabledStatusChangedEvent(existedUser, enabledDiff);
  }

  public EmailChangedEvent createEmailChangedEvent(User existedUser, StringDiff emailDiff) {
    return new EmailChangedEvent(existedUser, emailDiff);
  }

  public PermissionsChangedEvent createUpdatePermissionsEvent(User existedUser, StringSetDiff permissionsDiff) {
    List<Permission> permissions = dictionaryProvider.getPermissions();

    Map<String, String> permissionsHash = permissions.stream()
        .collect(Collectors.toMap(Permission::getName, Permission::getDescription));
    IdToNameConverter idToNameConverter = new IdToNameConverter(permissionsHash);

    String oldStrValue = getPermissionNames(permissionsDiff.getOldValue(), idToNameConverter);
    String newStrValue = getPermissionNames(permissionsDiff.getNewValue(), idToNameConverter);

    return new PermissionsChangedEvent(existedUser, new StringDiff(oldStrValue, newStrValue));
  }

  private String getPermissionNames(Set<String> keys, IdToNameConverter idToNameConverter) {
    return StringUtils.join(idToNameConverter.getNamesByIds(keys), ", ");
  }

  public UserRoleChangedEvent createUserRoleChangedEvent(User existedUser, StringSetDiff rolesDiff) {
    return new UserRoleChangedEvent(existedUser, rolesDiff);
  }

  public NotesChangedEvent createUpdateNotesEvent(User existedUser, StringDiff notesDiff) {
    return new NotesChangedEvent(existedUser, notesDiff);
  }

  @Autowired
  public void setDictionaryProvider(DictionaryProvider dictionaryProvider) {
    this.dictionaryProvider = dictionaryProvider;
  }
}
