package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserUnlockedEvent extends AdminCausedChangeLogEvent {

  public static final String EVENT_TYPE_USER_UNLOCKED = "Account Unlocked";
  static final String LOCKED = "Locked";
  static final String UNLOCKED = "Unlocked";

  private static final long serialVersionUID = 4063569795291472955L;

  public UserUnlockedEvent(User user) {
    super(user);
    setEventType(EVENT_TYPE_USER_UNLOCKED);
    setOldValue(LOCKED);
    setNewValue(UNLOCKED);
  }
}
