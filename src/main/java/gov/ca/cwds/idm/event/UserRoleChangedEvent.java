package gov.ca.cwds.idm.event;

import static gov.ca.cwds.config.api.idm.Roles.joinRoles;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRoleChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 1145582435628289251L;

  public static final String EVENT_TYPE_USER_ROLE_CHANGED = "USER'S ROLE CHANGED";

  public UserRoleChangedEvent(User user, Iterable<String> newRoles) {
    super(user);
    setEventType(EVENT_TYPE_USER_ROLE_CHANGED);
    setOldValue(joinRoles(user.getRoles()));
    setNewValue(joinRoles(newRoles));
  }

}
