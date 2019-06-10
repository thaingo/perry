package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRegistrationCompleteEvent extends UserChangeLogEvent {

  static final String EVENT_TYPE_USER_REGISTRATION_COMPLETE = "Registration Complete";
  static final String UNREGISTERED = "Un-Registered";
  static final String REGISTERED = "Registered";

  private static final long serialVersionUID = 3309243745034027420L;

  public UserRegistrationCompleteEvent(User user) {
    super(user);
    setEventType(EVENT_TYPE_USER_REGISTRATION_COMPLETE);
    setOldValue(UNREGISTERED);
    setNewValue(REGISTERED);
  }
}
