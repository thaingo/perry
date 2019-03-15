package gov.ca.cwds.idm.event;

import gov.ca.cwds.idm.dto.User;

public abstract class SystemCausedChangeLogEvent extends UserChangeLogEvent {

  static final String SYSTEM_USER_LOGIN = "System";

  SystemCausedChangeLogEvent(User user) {
    super(user);
    setUserLogin(SYSTEM_USER_LOGIN);
    setAdminName(SYSTEM_USER_LOGIN);
  }
}