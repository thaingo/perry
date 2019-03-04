package gov.ca.cwds.idm.event;

import static gov.ca.cwds.config.api.idm.Roles.joinRoles;
import static gov.ca.cwds.config.api.idm.Roles.replaceRoleIdByName;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserChangeLogRecord;
import java.time.LocalDateTime;


public abstract class UserChangeLogEvent extends AuditEvent<UserChangeLogRecord> {

  private static final long serialVersionUID = -2341018571605446028L;

  static final String CAP_EVENT_SOURCE = "CAP";

  UserChangeLogEvent(User user) {
    setTimestamp(LocalDateTime.now());
    setEventSource(CAP_EVENT_SOURCE);
    UserChangeLogRecord userChangeLogRecord = new UserChangeLogRecord();
    setEvent(userChangeLogRecord);
    setCountyName(user.getCountyName());
    setOfficeId(user.getOfficeId());
    setUserRoles(joinRoles(replaceRoleIdByName(user.getRoles())));
    setUserId(user.getId());
    setUserName(user.getFirstName() + " " + user.getLastName());
  }

  final void setAdminRole(String adminRole) {
    getEvent().setAdminRole(adminRole);
  }

  final  void setAdminName(String adminName) {
    getEvent().setAdminName(adminName);
  }

  final void setUserRoles(String userRoles) {
    getEvent().setUserRoles(userRoles);
  }

  final void setUserId(String userId) {
    getEvent().setUserId(userId);
  }

  final void setUserName(String userName) {
    getEvent().setUserName(userName);
  }

  final void setOldValue(String oldValue) {
    getEvent().setOldValue(oldValue);
  }

  final void setNewValue(String newValue) {
    getEvent().setNewValue(newValue);
  }

  final void setOfficeId(String officeId) {
    getEvent().setOfficeId(officeId);
  }

  private void setCountyName(String countyName) {
    getEvent().setCountyName(countyName);
  }

}
