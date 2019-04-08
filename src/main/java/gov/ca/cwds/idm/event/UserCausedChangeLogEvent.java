package gov.ca.cwds.idm.event;

import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;

public abstract class UserCausedChangeLogEvent extends UserChangeLogEvent {

  UserCausedChangeLogEvent(User user) {
    super(user);

    setUserLogin(user.getId());
    setAdminName(createUserFullNameString(user));
    setAdminRole(Roles.toRolesNamesString(user));
  }
}
