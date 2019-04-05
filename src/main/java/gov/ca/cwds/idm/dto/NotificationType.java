package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gov.ca.cwds.util.Utils;
import java.util.HashMap;
import java.util.Map;

public enum NotificationType {
  USER_LOCKED("locked");

  private static Map<String, NotificationType> strMap = new HashMap<>();

  static {
    strMap.put(USER_LOCKED.toString(), USER_LOCKED);
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
