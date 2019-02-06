package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.StringDiff;

/**
 * Created by Alexander Serbin on 1/18/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserEnabledStatusChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 6929792785662167658L;

  public static final String USER_ACCOUNT_STATUS_CHANGED = "Account Status";

  public UserEnabledStatusChangedEvent(User existedUser, StringDiff enabledStrDiff) {
    super(USER_ACCOUNT_STATUS_CHANGED, existedUser);
    setOldValue(enabledStrDiff.getOldValue());
    setNewValue(enabledStrDiff.getNewValue());
  }
}
