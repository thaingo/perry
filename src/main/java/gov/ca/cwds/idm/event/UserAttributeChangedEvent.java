package gov.ca.cwds.idm.event;

import static gov.ca.cwds.config.api.idm.Roles.joinRoles;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;

import gov.ca.cwds.idm.service.UserUpdateRequest;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
abstract class UserAttributeChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 897932343078261840L;

  UserAttributeChangedEvent(UserUpdateRequest userUpdateRequest) {
    super(userUpdateRequest.getUser());
    if (userUpdateRequest.isAttributeChanged(ROLES)) {
      setUserRoles(userUpdateRequest.getNewValueAsString(ROLES));
    } else {
      setUserRoles(joinRoles(userUpdateRequest.getUser().getRoles()));
    }
  }
}
