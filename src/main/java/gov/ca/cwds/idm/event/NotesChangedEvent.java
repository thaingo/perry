package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.StringDiff;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotesChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 88269111887681820L;

  public static final String EVENT_TYPE_NOTES_CHANGED = "Notes Changed";

  public NotesChangedEvent(User existedUser, StringDiff notesDiff) {
    super(EVENT_TYPE_NOTES_CHANGED, existedUser);
    setOldValue(notesDiff.getOldValue());
    setNewValue(notesDiff.getNewValue());
  }
}
