package gov.ca.cwds.idm.event;

import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;

public abstract class UserCausedChangeLogEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 1685967376478706898L;

  UserCausedChangeLogEvent(User user) {
    super(user);

    setUserLogin(user.getId());
    setAdminName(formatUserFullName(user));
    setAdminRole(Roles.toRolesNamesString(user));
  }
}
