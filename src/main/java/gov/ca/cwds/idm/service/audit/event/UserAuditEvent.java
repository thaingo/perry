package gov.ca.cwds.idm.service.audit.event;

import static gov.ca.cwds.config.api.idm.Roles.joinRoles;
import static gov.ca.cwds.config.api.idm.Roles.replaceRoleIdByName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserFullName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserChangeLogRecord;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 1/11/2019
 */

public class UserAuditEvent extends AuditEvent<UserChangeLogRecord> {

  private static final long serialVersionUID = -2341018571605446028L;

  public static final String CAP_EVENT_SOURCE = "CAP";

  public UserAuditEvent(String type, User user) {
    setEventType(type);
    setTimestamp(LocalDateTime.now());
    setEventSource(CAP_EVENT_SOURCE);
    setUserLogin(getCurrentUserName());
    UserChangeLogRecord userChangeLogRecord = new UserChangeLogRecord();
    userChangeLogRecord.setAdminName(getCurrentUserFullName());
    String adminRole = Roles
        .getRoleNameById(UserRolesService.getStrongestAdminRole(getCurrentUser()));
    userChangeLogRecord.setAdminRole(adminRole);
    setEvent(userChangeLogRecord);
    setCountyName(user.getCountyName());
    setOfficeId(user.getOfficeId());
    setUserRoles(joinRoles(replaceRoleIdByName(user.getRoles())));
    setUserId(user.getId());
    setUserName(user.getFirstName() + " " + user.getLastName());
  }

  protected void setAdminRole(String adminRole) {
    getEvent().setAdminRole(adminRole);
  }

  protected void setAdminName(String adminName) {
    getEvent().setAdminName(adminName);
  }

  public final void setUserRoles(String userRoles) {
    getEvent().setUserRoles(userRoles);
  }

  protected final void setUserId(String userId) {
    getEvent().setUserId(userId);
  }

  protected final void setUserName(String userName) {
    getEvent().setUserName(userName);
  }

  protected final void setOldValue(String oldValue) {
    getEvent().setOldValue(oldValue);
  }

  protected void setNewValue(String newValue) {
    getEvent().setNewValue(newValue);
  }

  protected final void setOfficeId(String officeId) {
    getEvent().setOfficeId(officeId);
  }

  protected final void setCountyName(String countyName) {
    getEvent().setCountyName(countyName);
  }

}
