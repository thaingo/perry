package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import org.apache.commons.lang3.Validate;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRoleChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 1145582435628289251L;

  public static final String EVENT_TYPE_USER_ROLE_CHANGED = "Role";

  public UserRoleChangedEvent(UserUpdateRequest userUpdateRequest) {
    super(userUpdateRequest.getExistedUser());
    Validate.isTrue(userUpdateRequest.isAttributeChanged(ROLES));
    setEventType(EVENT_TYPE_USER_ROLE_CHANGED);
    setUserRoles(userUpdateRequest.getNewValueAsString(ROLES));
    setOldValue(userUpdateRequest.getOldValueAsString(ROLES));
    setNewValue(userUpdateRequest.getNewValueAsString(ROLES));
  }

}
