package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserCreatedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 1527655053336674520L;

  public static final String EVENT_TYPE_USER_CREATED = "User Created";

  public UserCreatedEvent(User user) {
    super(user);
    setEventType(EVENT_TYPE_USER_CREATED);
    setNewValue(Roles.joinRoles(user.getRoles()));
    setUserRoles(Roles.joinRoles(user.getRoles()));
  }

}
