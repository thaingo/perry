package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.OtherUserAttribute.ENABLED_STATUS;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserEnabledStatusAttributeDiff;
import org.apache.commons.lang3.Validate;

/**
 * Created by Alexander Serbin on 1/18/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserEnabledStatusChangedEvent extends UserAttributeChangedEvent {

  private static final long serialVersionUID = 6929792785662167658L;
  static final String ACTIVE = "Active";
  static final String INACTIVE = "Inactive";
  static final String USER_ACCOUNT_STATUS_CHANGED = "Account Status";

  public UserEnabledStatusChangedEvent(UserUpdateRequest userUpdateRequest) {
    super(userUpdateRequest);
    setEventType(USER_ACCOUNT_STATUS_CHANGED);
    Validate.isTrue(userUpdateRequest.isAttributeChanged(ENABLED_STATUS));
    UserEnabledStatusAttributeDiff diff = (UserEnabledStatusAttributeDiff) userUpdateRequest
        .getDiffMap().get(ENABLED_STATUS);
    setOldValue(getValueAsString(diff.getOldValue()));
    setNewValue(getValueAsString(diff.getNewValue()));
  }

  private static String getValueAsString(Boolean value) {
    return value ? ACTIVE : INACTIVE;
  }

}
