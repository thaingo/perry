package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.OtherUserAttribute.ENABLED_STATUS;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import org.apache.commons.lang3.Validate;

/**
 * Created by Alexander Serbin on 1/18/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserEnabledStatusChangedEvent extends UserAttributeChangedEvent {

  private static final long serialVersionUID = 6929792785662167658L;

  public static final String USER_ACCOUNT_STATUS_CHANGED = "User Account Status Changed";

  public UserEnabledStatusChangedEvent(UserUpdateRequest userUpdateRequest) {
    super(userUpdateRequest);
    setEventType(USER_ACCOUNT_STATUS_CHANGED);
    Validate.isTrue(userUpdateRequest.isAttributeChanged(ENABLED_STATUS));
    setOldValue(userUpdateRequest.getOldValueAsString(ENABLED_STATUS));
    setNewValue(userUpdateRequest.getNewValueAsString(ENABLED_STATUS));
  }
}
