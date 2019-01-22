package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.OtherUserAttribute.ACCOUNT_STATUS;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import org.apache.commons.lang3.Validate;

/**
 * Created by Alexander Serbin on 1/18/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserAccountStatusChangedEvent extends UserAttributeChangedEvent {

  private static final long serialVersionUID = 6929792785662167658L;

  public static final String USER_ACCOUNT_STATUS_CHANGED = "User Account Status Changed";

  public UserAccountStatusChangedEvent(UserUpdateRequest userUpdateRequest) {
    super(userUpdateRequest);
    setEventType(USER_ACCOUNT_STATUS_CHANGED);
    Validate.isTrue(userUpdateRequest.isAttributeChanged(ACCOUNT_STATUS));
    setOldValue(userUpdateRequest.getOldValueAsString(ACCOUNT_STATUS));
    setNewValue(userUpdateRequest.getNewValueAsString(ACCOUNT_STATUS));
  }
}
