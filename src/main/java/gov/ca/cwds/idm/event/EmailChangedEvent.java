package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import org.apache.commons.lang3.Validate;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmailChangedEvent extends UserAttributeChangedEvent {

  private static final long serialVersionUID = 5740708683905011516L;

  public static final String EVENT_TYPE_EMAIL_CHANGED = "User Email Address Changed";

  public EmailChangedEvent(UserUpdateRequest userUpdateRequest) {
    super(userUpdateRequest);
    Validate.isTrue(userUpdateRequest.isAttributeChanged(EMAIL));
    setEventType(EVENT_TYPE_EMAIL_CHANGED);
    setOldValue(userUpdateRequest.getOldValueAsString(EMAIL));
    setNewValue(userUpdateRequest.getNewValueAsString(EMAIL));
  }

}
