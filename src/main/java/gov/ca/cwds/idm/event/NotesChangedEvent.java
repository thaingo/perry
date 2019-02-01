package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.DatabaseUserAttribute.NOTES;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import org.apache.commons.lang3.Validate;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotesChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 88269111887681820L;

  public static final String EVENT_TYPE_NOTES_CHANGED = "Notes Changed";

  public NotesChangedEvent(UserUpdateRequest userUpdateRequest) {
    super(userUpdateRequest.getExistedUser());
    Validate.isTrue(userUpdateRequest.isAttributeChanged(NOTES));
    setEventType(EVENT_TYPE_NOTES_CHANGED);
    setOldValue(userUpdateRequest.getOldValueAsString(NOTES));
    setNewValue(userUpdateRequest.getNewValueAsString(NOTES));
  }
}
