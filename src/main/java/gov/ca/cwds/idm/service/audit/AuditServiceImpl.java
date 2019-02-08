package gov.ca.cwds.idm.service.audit;

import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.EVENT_TYPE_EMAIL_CHANGED;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.EVENT_TYPE_NOTES_CHANGED;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.EVENT_TYPE_REGISTRATION_RESENT;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.EVENT_TYPE_USER_CREATED;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.audit.event.AuditEvent;
import gov.ca.cwds.idm.service.audit.event.UserAuditEvent;
import gov.ca.cwds.idm.service.audit.event.UserPropertyChangedAuditEvent;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
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
    UserAuditEvent event = auditEventFactory.createUserEvent(EVENT_TYPE_USER_CREATED, user);
    auditLogService.createAuditLogRecord(event);
  }

  @Override
  public void auditUserRegistrationResent(User user) {
    UserAuditEvent event = auditEventFactory.createUserEvent(EVENT_TYPE_REGISTRATION_RESENT, user);
    auditLogService.createAuditLogRecord(event);
  }

  @Override
  public void auditUserEnableStatusUpdate(User existedUser, BooleanDiff enabledDiff) {
    UserPropertyChangedAuditEvent event =
        auditEventFactory.createUserEnableStatusChangedEvent(existedUser, enabledDiff);
    auditLogService.createAuditLogRecord(event);
  }

  @Override
  public void auditUserUpdate(UserUpdateRequest userUpdateRequest) {
    User existedUser = userUpdateRequest.getExistedUser();
    UpdateDifference updateDifference = userUpdateRequest.getUpdateDifference();

    publishUpdateRolesEvent(existedUser, updateDifference.getRolesDiff());
    publishUpdatePermissionsEvent(existedUser, updateDifference.getPermissionsDiff());
    publishUpdateEmailEvent(existedUser, updateDifference.getEmailDiff());
    publishUpdateNotesEvent(existedUser, updateDifference.getNotesDiff());
  }

  private void publishUpdateEmailEvent(User existedUser, Optional<StringDiff> optEmailDiff) {
    optEmailDiff.ifPresent(emailDiff -> {
      AuditEvent event = auditEventFactory
          .createUserStringPropertyChangedEvent(EVENT_TYPE_EMAIL_CHANGED, existedUser, emailDiff);
      auditLogService.createAuditLogRecord(event);
    });
  }

  private void publishUpdatePermissionsEvent(User existedUser,
      Optional<StringSetDiff> optPermissionsDiff) {

    optPermissionsDiff.ifPresent(permissionsDiff -> {
      AuditEvent event =
          auditEventFactory.createUserPermissionsChangedEvent(existedUser, permissionsDiff);
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
      AuditEvent event = auditEventFactory
          .createUserStringPropertyChangedEvent(EVENT_TYPE_NOTES_CHANGED, existedUser, notesDiff);
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
