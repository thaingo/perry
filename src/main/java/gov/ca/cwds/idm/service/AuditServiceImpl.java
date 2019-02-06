package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.AuditEvent;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.Differencing;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class AuditServiceImpl implements AuditService {

  private AuditLogService auditLogService;

  private AuditEventFactory auditEventFactory;

  @Override
  public void auditUserCreate(User user) {
    AuditEvent event = auditEventFactory.createAuditUserCreateEvent(user);
    auditLogService.createAuditLogRecord(event);
  }

  @Override
  public void auditUserRegistrationResent(User user) {
    AuditEvent event = auditEventFactory.createUserRegistrationResentEvent(user);
    auditLogService.createAuditLogRecord(event);
  }

  @Override
  public void auditUserEnableStatusUpdate(User existedUser, BooleanDiff enabledDiff) {
    AuditEvent event =
        auditEventFactory.createUserEnableStatusUpdateEvent(existedUser, enabledDiff);
    auditLogService.createAuditLogRecord(event);
  }

  @Override
  public void auditUserUpdate(UserUpdateRequest userUpdateRequest) {
    User existedUser = userUpdateRequest.getExistedUser();
    Differencing differencing = userUpdateRequest.getDifferencing();

    publishUpdateRolesEvent(existedUser, differencing.getRolesDiff());
    publishUpdatePermissionsEvent(existedUser, differencing.getPermissionsDiff());
    publishUpdateEmailEvent(existedUser, differencing.getEmailDiff());
    publishUpdateNotesEvent(existedUser, differencing.getNotesDiff());
  }

  private void publishUpdateEmailEvent(User existedUser, Optional<StringDiff> optEmailDiff) {
    optEmailDiff.ifPresent(emailDiff -> {
      AuditEvent event = auditEventFactory.createEmailChangedEvent(existedUser, emailDiff);
      auditLogService.createAuditLogRecord(event);
    });
  }

  private void publishUpdatePermissionsEvent(User existedUser,
      Optional<StringSetDiff> optPermissionsDiff) {

    optPermissionsDiff.ifPresent(permissionsDiff -> {
      AuditEvent event =
          auditEventFactory.createUpdatePermissionsEvent(existedUser, permissionsDiff);
      auditLogService.createAuditLogRecord(event);
    });
  }

  private void publishUpdateRolesEvent(User existedUser, Optional<StringSetDiff> optRolesDiff) {
    optRolesDiff.ifPresent(rolesDiff -> {
      AuditEvent event = auditEventFactory.createUserRoleChangedEvent(existedUser, rolesDiff);
      auditLogService.createAuditLogRecord(event);
    });
  }

  private void publishUpdateNotesEvent(User existedUser, Optional<StringDiff> optNotesDiff) {
    optNotesDiff.ifPresent(notesDiff -> {
      AuditEvent event = auditEventFactory.createUpdateNotesEvent(existedUser, notesDiff);
      auditLogService.createAuditLogRecord(event);
    });
  }

  @Autowired
  public void setAuditLogService(AuditLogService auditLogService) {
    this.auditLogService = auditLogService;
  }

  @Autowired
  public void setAuditEventFactory(AuditEventFactory auditEventFactory) {
    this.auditEventFactory = auditEventFactory;
  }
}
