package gov.ca.cwds.util;

import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserFullName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import gov.ca.cwds.idm.dto.AuditEvent;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserChangeLogRecord;
import gov.ca.cwds.idm.event.UserCreatedEvent;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import java.util.UUID;

public class AuditLogsUtil {

  public static final String CAP_EVENT_SOURCE = "CAP";
  public static final String USER_CREATED_EVENT_TYPE = "User Created";

  private AuditLogsUtil() {
  }

  public static AuditEvent composeAuditEvent(UserCreatedEvent event) {
    User user = event.getUser();

    AuditEvent<UserChangeLogRecord> auditEvent = new AuditEvent<>();

    auditEvent.setTimestamp(event.getCreatedDateTime());
    auditEvent.setEventSource(CAP_EVENT_SOURCE);
    auditEvent.setEventType(USER_CREATED_EVENT_TYPE);
    auditEvent.setUserLogin(getCurrentUserName());
    auditEvent.setId(UUID.randomUUID().toString());

    UserChangeLogRecord userChangeLogRecord = new UserChangeLogRecord();
    userChangeLogRecord.setCountyName(user.getCountyName());
    userChangeLogRecord.setOfficeId(user.getOfficeId());
    userChangeLogRecord.setUserId(user.getId());
    if (user.getRoles() != null) {
      userChangeLogRecord.getUserRoles().addAll(user.getRoles());
      userChangeLogRecord.setNewValue(String.join(", ", user.getRoles()));
    }
    userChangeLogRecord.setAdminName(getCurrentUserFullName());

    String adminRole = UserRolesService.getStrongestAdminRole(getCurrentUser());
    userChangeLogRecord.setAdminRole(adminRole);

    auditEvent.setEvent(userChangeLogRecord);

    return auditEvent;
  }
}
