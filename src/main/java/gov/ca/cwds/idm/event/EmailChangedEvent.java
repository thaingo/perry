package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.Diff;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmailChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 5740708683905011516L;

  public static final String EVENT_TYPE_EMAIL_CHANGED = "Email Address";

  public EmailChangedEvent(User existedUser, Diff emailDiff) {
    super(existedUser);
    setEventType(EVENT_TYPE_EMAIL_CHANGED);
    setOldValue(emailDiff.getOldValueAsString());
    setNewValue(emailDiff.getNewValueAsString());
  }
}
