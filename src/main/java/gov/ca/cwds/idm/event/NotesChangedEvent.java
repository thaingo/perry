package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.Diff;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotesChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 88269111887681820L;

  public static final String EVENT_TYPE_NOTES_CHANGED = "Notes Changed";

  public NotesChangedEvent(User existedUser, Diff notesDiff) {
    super(existedUser);
    setEventType(EVENT_TYPE_NOTES_CHANGED);
    setOldValue(notesDiff.getOldValueAsString());
    setNewValue(notesDiff.getNewValueAsString());
  }
}
