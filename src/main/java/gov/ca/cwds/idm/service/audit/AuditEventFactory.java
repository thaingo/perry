package gov.ca.cwds.idm.service.audit;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.audit.event.UserAuditEvent;
import gov.ca.cwds.idm.service.audit.event.UserPropertyChangedAuditEvent;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;

public interface AuditEventFactory {

  UserAuditEvent createUserEvent(String type, User user);

  UserPropertyChangedAuditEvent createUserStringPropertyChangedEvent(
      String type, User existedUser, StringDiff strPropertyDiff);

  UserPropertyChangedAuditEvent createUserEnableStatusUpdateEvent(User existedUser,
      BooleanDiff enabledDiff);

  UserPropertyChangedAuditEvent createUpdatePermissionsEvent(User existedUser,
      StringSetDiff permissionsDiff);

  UserPropertyChangedAuditEvent createUserRoleChangedEvent(User existedUser,
      StringSetDiff rolesDiff);
}
