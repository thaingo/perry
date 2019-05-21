package gov.ca.cwds.idm.dto;

import gov.ca.cwds.idm.event.AuditEvent;
import gov.ca.cwds.idm.event.UserLockedEvent;
import gov.ca.cwds.idm.event.UserPasswordChangedEvent;
import gov.ca.cwds.idm.event.UserRegistrationCompleteEvent;
import gov.ca.cwds.util.Utils;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"fb-contrib:STT_TOSTRING_STORED_IN_FIELD"})
public enum NotificationType {

  USER_LOCKED("locked") {
    @Override
    public AuditEvent createAuditEvent(User user) {
      return new UserLockedEvent(user);
    }
  },
  USER_PASSWORD_CHANGED("user-password-changed") {
    @Override
    public AuditEvent createAuditEvent(User user) {
      return new UserPasswordChangedEvent(user);
    }
  },
  USER_REGISTRATION_COMPLETE("registration-complete") {
    @Override
    public AuditEvent createAuditEvent(User user) {
      return new UserRegistrationCompleteEvent(user);
    }
  };

  private static Map<String, NotificationType> strMap = new HashMap<>();
  static {
    for (NotificationType notificationType : NotificationType.values()) {
      strMap.put(notificationType.toString(), notificationType);
    }
  }

  private String str;

  NotificationType(String str) {
    this.str = str;
  }

  public static NotificationType forString(String str) {
    return strMap.get(Utils.toLowerCase(str));
  }

  @Override
  public final String toString() {
    return str;
  }

  public abstract AuditEvent createAuditEvent(User user);
}
