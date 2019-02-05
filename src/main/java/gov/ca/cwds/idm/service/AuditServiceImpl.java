package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.EmailChangedEvent;
import gov.ca.cwds.idm.event.NotesChangedEvent;
import gov.ca.cwds.idm.event.PermissionsChangedEvent;
import gov.ca.cwds.idm.event.UserCreatedEvent;
import gov.ca.cwds.idm.event.UserEnabledStatusChangedEvent;
import gov.ca.cwds.idm.event.UserRegistrationResentEvent;
import gov.ca.cwds.idm.event.UserRoleChangedEvent;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.Differencing;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class AuditServiceImpl implements AuditService {

  private AuditLogService auditLogService;

  private DictionaryProvider dictionaryProvider;

  @Override
  public void auditUserCreate(User user) {
    auditLogService.createAuditLogRecord(new UserCreatedEvent(user));
  }

  @Override
  public void auditUserRegistrationResent(User user) {
    auditLogService.createAuditLogRecord(new UserRegistrationResentEvent(user));
  }

  @Override
  public void auditUserEnableStatusUpdate(User existedUser, BooleanDiff enabledDiff){
    auditLogService
        .createAuditLogRecord(new UserEnabledStatusChangedEvent(existedUser, enabledDiff));
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
    optEmailDiff.ifPresent(emailDiff ->
        auditLogService.createAuditLogRecord(new EmailChangedEvent(existedUser, emailDiff)));
  }

  private void publishUpdatePermissionsEvent(User existedUser,
      Optional<StringSetDiff> optPermissionsDiff) {
    List<Permission> permissions = dictionaryProvider.getPermissions();

    optPermissionsDiff.ifPresent(permissionsDiff ->
        auditLogService.createAuditLogRecord(
            new PermissionsChangedEvent(existedUser, permissionsDiff, permissions)));
  }

  private void publishUpdateRolesEvent(User existedUser, Optional<StringSetDiff> optRolesDiff) {
    optRolesDiff.ifPresent(rolesDiff ->
        auditLogService.createAuditLogRecord(new UserRoleChangedEvent(existedUser, rolesDiff)));
  }

  private void publishUpdateNotesEvent(User existedUser, Optional<StringDiff> optNotesDiff) {
    optNotesDiff.ifPresent(notesDiff ->
        auditLogService.createAuditLogRecord(new NotesChangedEvent(existedUser, notesDiff)));
  }

  @Autowired
  public void setAuditLogService(AuditLogService auditLogService) {
    this.auditLogService = auditLogService;
  }

  @Autowired
  public void setDictionaryProvider(DictionaryProvider dictionaryProvider) {
    this.dictionaryProvider = dictionaryProvider;
  }
}
