package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.StringDiff;


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmailChangedEvent extends AdminCausedChangeLogEvent {

  private static final long serialVersionUID = 5740708683905011516L;

  public static final String EVENT_TYPE_EMAIL_CHANGED = "Email Address";

  public EmailChangedEvent(User existedUser, StringDiff emailDiff) {
    super(existedUser);
    setEventType(EVENT_TYPE_EMAIL_CHANGED);
    setOldValue(emailDiff.getOldValue());
    setNewValue(emailDiff.getNewValue());
  }
}
