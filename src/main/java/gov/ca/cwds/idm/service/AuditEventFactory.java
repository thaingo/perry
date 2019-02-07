package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.UserAuditEvent;
import gov.ca.cwds.idm.event.UserPropertyChangedAuditEvent;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;

public interface AuditEventFactory {

  UserAuditEvent createUserEvent(String type, User user);

  UserPropertyChangedAuditEvent createUserPropertyChangedEvent(
      String type, User existedUser, StringDiff strPropertyDiff);

  UserPropertyChangedAuditEvent createUserEnableStatusUpdateEvent(User existedUser,
      BooleanDiff enabledDiff);

  UserPropertyChangedAuditEvent createUpdatePermissionsEvent(User existedUser,
      StringSetDiff permissionsDiff);

  UserPropertyChangedAuditEvent createUserRoleChangedEvent(User existedUser,
      StringSetDiff rolesDiff);
}
