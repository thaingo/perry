package gov.ca.cwds.idm.event;

import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserFullName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserChangeLogRecord;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 1/11/2019
 */

public abstract class UserChangeLogEvent extends AuditEvent<UserChangeLogRecord> implements Serializable {

  private static final long serialVersionUID = -2341018571605446028L;

  static final String CAP_EVENT_SOURCE = "CAP";

  public UserChangeLogEvent(User user) {
    setTimestamp(LocalDateTime.now());
    setEventSource(CAP_EVENT_SOURCE);
    setUserLogin(getCurrentUserName());
    UserChangeLogRecord userChangeLogRecord = new UserChangeLogRecord();
    userChangeLogRecord.setCountyName(user.getCountyName());
    userChangeLogRecord.setOfficeId(user.getOfficeId());
    userChangeLogRecord.setUserId(user.getId());
    userChangeLogRecord.setUserName(user.getFirstName() + " " + user.getLastName());
    userChangeLogRecord.setAdminName(getCurrentUserFullName());
    String adminRole = UserRolesService.getStrongestAdminRole(getCurrentUser());
    userChangeLogRecord.setAdminRole(adminRole);
    setEvent(userChangeLogRecord);
  }

}
