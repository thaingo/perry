package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserPasswordChangedEvent extends UserChangeLogEvent {

  public static final String EVENT_TYPE_USER_PASSWORD_CHANGED = "User Password Changed";

  private static final long serialVersionUID = 3965833038876340243L;

  public UserPasswordChangedEvent(User user) {
    super(user);
    setEventType(EVENT_TYPE_USER_PASSWORD_CHANGED);
  }
}
