package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.StringDiff;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRoleChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 1145582435628289251L;

  public static final String EVENT_TYPE_USER_ROLE_CHANGED = "Role";

  public UserRoleChangedEvent(User existedUser, StringDiff strRolesDiff) {
    super(EVENT_TYPE_USER_ROLE_CHANGED, existedUser);
    setOldValue(strRolesDiff.getOldValue());
    setNewValue(strRolesDiff.getNewValue());
  }
}
