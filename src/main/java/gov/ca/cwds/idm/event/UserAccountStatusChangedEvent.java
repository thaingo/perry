package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.service.UserUpdateRequest;

/**
 * Created by Alexander Serbin on 1/18/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserAccountStatusChangedEvent extends UserAttributeChangedEvent {

  private static final long serialVersionUID = 6929792785662167658L;

  public static final String USER_ACCOUNT_STATUS_CHANGED = "User Account Status Changed";

  public UserAccountStatusChangedEvent(UserUpdateRequest userUpdateRequest) {
    super(userUpdateRequest);
    /*
    setEventType(USER_ACCOUNT_STATUS_CHANGED);
    setUserRoles(joinRoles(replaceRoleIdByName(user.getRoles())));
    setOldValue(user.getStatus() diffMap.get(PERMISSIONS).getOldValueAsString());
    setNewValue(diffMap.get(PERMISSIONS).getNewValueAsString());
*/
  }
}
