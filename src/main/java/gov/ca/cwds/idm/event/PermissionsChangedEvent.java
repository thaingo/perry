package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import org.apache.commons.lang3.Validate;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PermissionsChangedEvent extends UserAttributeChangedEvent {

  private static final long serialVersionUID = 5084376904261765730L;

  public static final String EVENT_TYPE_PERMISSIONS_CHANGED = "User Permissions Changed";

  public PermissionsChangedEvent(UserUpdateRequest userUpdateRequest) {
    super(userUpdateRequest);
    Validate.isTrue(userUpdateRequest.isAttributeChanged(PERMISSIONS));
    setEventType(EVENT_TYPE_PERMISSIONS_CHANGED);
    setOldValue(userUpdateRequest.getOldValueAsString(PERMISSIONS));
    setNewValue(userUpdateRequest.getNewValueAsString(PERMISSIONS));
  }

}
