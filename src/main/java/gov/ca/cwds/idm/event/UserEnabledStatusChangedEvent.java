package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.BooleanDiff;

/**
 * Created by Alexander Serbin on 1/18/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserEnabledStatusChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 6929792785662167658L;
  static final String ACTIVE = "Active";
  static final String INACTIVE = "Inactive";
  static final String USER_ACCOUNT_STATUS_CHANGED = "Account Status";

  public UserEnabledStatusChangedEvent(User existedUser, BooleanDiff enabledDiff) {
    super(existedUser);
    setEventType(USER_ACCOUNT_STATUS_CHANGED);

    setOldValue(getValueAsString(enabledDiff.getOldValue()));
    setNewValue(getValueAsString(enabledDiff.getNewValue()));
  }

  private static String getValueAsString(Boolean value) {
    return value ? ACTIVE : INACTIVE;
  }
}
