package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gov.ca.cwds.util.Utils;
import java.util.HashMap;
import java.util.Map;

public enum NotificationType {
  USER_LOCKED("locked"),
  USER_PASSWORD_CHANGED("user-password-changed");

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

  @JsonCreator
  public static NotificationType forString(String str) {
    return strMap.get(Utils.toLowerCase(str));
  }

  @Override
  @JsonValue
  public final String toString() {
    return str;
  }
}
