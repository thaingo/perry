package gov.ca.cwds.idm.service.audit;

import static gov.ca.cwds.config.api.idm.Roles.replaceRoleIdByName;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.audit.event.UserAuditEvent;
import gov.ca.cwds.idm.service.audit.event.UserPropertyChangedAuditEvent;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.DictionaryProvider;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import gov.ca.cwds.util.IdToNameConverter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class AuditEventFactoryImpl implements AuditEventFactory {

  public static final String EVENT_TYPE_USER_CREATED = "User Created";
  public static final String EVENT_TYPE_REGISTRATION_RESENT = "Registration Resent";

  public static final String EVENT_TYPE_USER_ROLE_CHANGED = "Role";
  public static final String EVENT_TYPE_USER_ENABLED_STATUS_CHANGED = "Account Status";
  public static final String EVENT_TYPE_EMAIL_CHANGED = "Email Address";
  public static final String EVENT_TYPE_PERMISSIONS_CHANGED = "Permission Change";
  public static final String EVENT_TYPE_NOTES_CHANGED = "Notes Changed";

  public static final String ACTIVE = "Active";
  public static final String INACTIVE = "Inactive";

  private DictionaryProvider dictionaryProvider;

  @Override
  public UserAuditEvent createUserEvent(String type, User user) {
    return new UserAuditEvent(type, user);
  }

  @Override
  public UserPropertyChangedAuditEvent createUserStringPropertyChangedEvent(
      String type, User existedUser, StringDiff strPropertyDiff) {
    return new UserPropertyChangedAuditEvent(type, existedUser, strPropertyDiff);
  }

  @Override
  public UserPropertyChangedAuditEvent createUserEnableStatusChangedEvent(User existedUser,
      BooleanDiff enabledDiff) {
    String enabledOldStringValue = getEnabledValueAsString(enabledDiff.getOldValue());
    String enabledNewStringValue = getEnabledValueAsString(enabledDiff.getNewValue());
    StringDiff enabledStringDiff = new StringDiff(enabledOldStringValue, enabledNewStringValue);
    return new UserPropertyChangedAuditEvent(EVENT_TYPE_USER_ENABLED_STATUS_CHANGED, existedUser, enabledStringDiff);
  }

  private static String getEnabledValueAsString(Boolean value) {
    return value ? ACTIVE : INACTIVE;
  }

  @Override
  public UserPropertyChangedAuditEvent createUserPermissionsChangedEvent(User existedUser,
      StringSetDiff permissionsDiff) {
    List<Permission> permissions = dictionaryProvider.getPermissions();

    Map<String, String> permissionsHash = permissions.stream()
        .collect(Collectors.toMap(Permission::getName, Permission::getDescription));
    IdToNameConverter idToNameConverter = new IdToNameConverter(permissionsHash);

    String oldStrValue = getPermissionNames(permissionsDiff.getOldValue(), idToNameConverter);
    String newStrValue = getPermissionNames(permissionsDiff.getNewValue(), idToNameConverter);

    return new UserPropertyChangedAuditEvent(EVENT_TYPE_PERMISSIONS_CHANGED, existedUser,
        new StringDiff(oldStrValue, newStrValue));
  }

  private String getPermissionNames(Set<String> keys, IdToNameConverter idToNameConverter) {
    return StringUtils.join(idToNameConverter.getNamesByIds(keys), ", ");
  }

  @Override
  public UserPropertyChangedAuditEvent createUserRoleChangedEvent(User existedUser,
      StringSetDiff rolesDiff) {

    String oldRolesStrValue = convertRoleKeysToNamesString(rolesDiff.getOldValue());
    String newRolesStrValue = convertRoleKeysToNamesString(rolesDiff.getNewValue());

    UserPropertyChangedAuditEvent event =
        new UserPropertyChangedAuditEvent(EVENT_TYPE_USER_ROLE_CHANGED, existedUser,
        new StringDiff(oldRolesStrValue, newRolesStrValue));

    event.setUserRoles(newRolesStrValue);
    return event;
  }

  static String convertRoleKeysToNamesString(Set<String> roleKeys) {
    return toCommaDelimitedString(replaceRoleIdByName(roleKeys));
  }

  private static String toCommaDelimitedString(Set<String> value) {
    if (value == null) {
      return "";
    } else {
      return StringUtils.join(new TreeSet<>(value), ", ");
    }
  }

  @Autowired
  public void setDictionaryProvider(DictionaryProvider dictionaryProvider) {
    this.dictionaryProvider = dictionaryProvider;
  }
}
