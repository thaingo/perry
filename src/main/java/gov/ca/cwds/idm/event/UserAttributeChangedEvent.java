package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;

import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import java.util.Map;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
abstract class UserAttributeChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 897932343078261840L;

  UserAttributeChangedEvent(User user, Map<UserAttribute, UserAttributeDiff> diffMap) {
    super(user);
    if (diffMap.containsKey(ROLES)) {
        setUserRoles(diffMap.get(ROLES).getNewValueAsString());
    } else {
      setUserRoles(Roles.joinRoles(user.getRoles()));
    }
  }
}
