package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestAdminRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCalsExternalWorker;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCountyAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isSuperAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isUserWithMainRole;
import static gov.ca.cwds.service.messages.MessageCode.ADMIN_CANNOT_UPDATE_HIMSELF;
import static gov.ca.cwds.service.messages.MessageCode.CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.service.messages.MessageCode.STATE_ADMIN_ROLES_CANNOT_BE_EDITED;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_UPDATE_UNALLOWED_ROLES;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;
import static gov.ca.cwds.util.Utils.toCommaDelimitedString;
import static java.util.Arrays.asList;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.exception.UserValidationException;
import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.rule.ErrorRule;
import gov.ca.cwds.idm.service.rule.ErrorRuleList;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.util.Utils;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class AbstractAdminActionsAuthorizer implements AdminActionsAuthorizer {

  private User user;

  private ExceptionFactory exceptionFactory;

  AbstractAdminActionsAuthorizer(User user) {
    this.user = user;
  }

  protected User getUser() {
    return user;
  }

  public abstract ErrorRuleList getViewUserRules();

  @Override
  public final void checkCanViewUser() {
    getViewUserRules().check();
  }

  public abstract ErrorRuleList getCreateUserRules();

  @Override
  public final void checkCanCreateUser() {
    getCreateUserRules().check();
  }

  public abstract ErrorRuleList getResendInvitationMessageRules();

  @Override
  public final void checkCanResendInvitationMessage() {
    getResendInvitationMessageRules().check();
  }

  public abstract ErrorRuleList getUpdateUserRules(UserUpdate userUpdate);

  @Override
  public final void checkCanUpdateUser(UserUpdate userUpdate) {
    getUpdateUserRules(userUpdate).check();
  }

  @Override
  public final boolean canUpdateUser(UserUpdate userUpdate) {
    return !getUpdateUserRules(userUpdate).hasError();
  }

  protected final ErrorRule userAndAdminAreNotTheSameUser() {
    return new ErrorRule(
        this::userAndAdminAreTheSameUser,
        () -> createAuthorizationException(ADMIN_CANNOT_UPDATE_HIMSELF)
    );
  }

  protected final ErrorRule userIsNotSuperAdmin(MessageCode errorCode) {
    return new ErrorRule(
        this::isUserSuperAdmin,
        () -> createAuthorizationException(
            errorCode, getStrongestAdminRole(getCurrentUser()), getUser().getId()));
  }

  protected final ErrorRule userIsNotStateAdmin(MessageCode errorCode) {
    return new ErrorRule(this::isUsersStateAdmin,
        () -> createAuthorizationException(errorCode, getUser().getId()));
  }

  protected final ErrorRule userIsNotCountyAdmin(MessageCode errorCode) {
    return new ErrorRule(
        this::isUserCountyAdmin,
        () -> createAuthorizationException(errorCode, getUser().getId()));
  }

  protected final ErrorRule userIsNotCalsExternalWorker(MessageCode errorCode) {
    return new ErrorRule(
        this::isUserCalsExternalWorker,
        () -> createAuthorizationException(errorCode, getUser().getId()));
  }

  protected final ErrorRule adminAndUserAreInTheSameCounty(MessageCode errorCode, String... args) {
    return new ErrorRule(
        this::areAdminAndUserNotInTheSameCounty,
        () -> createAuthorizationException(errorCode, args));
  }

  protected final ErrorRule adminAndUserAreInTheSameOffice(MessageCode errorCode) {
    return new ErrorRule(
        this::adminAndUserAreInDifferentOffices,
        () -> createAuthorizationException(errorCode, getUser().getId()));
  }

  private boolean adminAndUserAreInDifferentOffices() {
    String userOfficeId = getUser().getOfficeId();
    Set<String> adminOfficeIds = getCurrentUserOfficeIds();
    return adminOfficeIds == null || userOfficeId == null || !adminOfficeIds.contains(userOfficeId);
  }

  protected final ErrorRule stateAdminUserRolesAreNotChanged(UserUpdate userUpdate) {
    return rolesAreNotEdited(STATE_ADMIN, userUpdate, STATE_ADMIN_ROLES_CANNOT_BE_EDITED);
  }

  protected final ErrorRule calsExternalWorkerRolesAreNotChanged(UserUpdate userUpdate) {
    return rolesAreNotEdited(CALS_EXTERNAL_WORKER, userUpdate,
        CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER);
  }

  private ErrorRule rolesAreNotEdited(String userMainRole, UserUpdate userUpdate,
      MessageCode errorCode) {
    return new ErrorRule(
        () -> userUpdate.getRoles() != null
            && isUserWithMainRole(getUser(), userMainRole)
            && !Utils.toSet(userMainRole).equals(userUpdate.getRoles()),
        () -> createAuthorizationException(errorCode, getUser().getId()));
  }

  private boolean areAdminAndUserNotInTheSameCounty() {
    String userCountyName = getUser().getCountyName();
    String adminCountyName = getCurrentUserCountyName();
    return userCountyName == null || !userCountyName.equals(adminCountyName);
  }

  protected final ErrorRule createdUserRolesMayBe(String... allowedRoles) {
    Collection<String> roles = getUser().getRoles();
    List<String> allowedRolesList = asList(allowedRoles);

    return new ErrorRule(
        () -> roles != null && !allowedRolesList.containsAll(roles),
        () -> createValidationException(
            UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES,
            toCommaDelimitedString(roles),
            toCommaDelimitedString(allowedRolesList))
    );
  }

  protected final ErrorRule cwsWorkerRolesMayBeChangedTo(UserUpdate userUpdate,
      String... allowedRoles) {
    return userChangesRolesOnlyTo(CWS_WORKER, userUpdate, allowedRoles);
  }

  protected final ErrorRule officeAdminUserRolesMayBeChangedTo(UserUpdate userUpdate,
      String... allowedRoles) {
    return userChangesRolesOnlyTo(OFFICE_ADMIN, userUpdate, allowedRoles);
  }

  protected final ErrorRule countyAdminUserRolesMayBeChangedTo(UserUpdate userUpdate,
      String... allowedRoles) {
    return userChangesRolesOnlyTo(COUNTY_ADMIN, userUpdate, allowedRoles);
  }

  protected final ErrorRule stateAdminUserRolesMayBeChangedTo(UserUpdate userUpdate,
      String... allowedRoles) {
    return userChangesRolesOnlyTo(STATE_ADMIN, userUpdate, allowedRoles);
  }

  private ErrorRule userChangesRolesOnlyTo(String userCurrentRole, UserUpdate userUpdate,
      String... allowedRoles) {
    Set<String> newRoles = userUpdate.getRoles();
    List<String> allowedRolesList = asList(allowedRoles);

    return new ErrorRule(
        () ->
            newRoles != null
                && isUserWithMainRole(getUser(), userCurrentRole)
                && (!allowedRolesList.containsAll(newRoles)),
        () ->
            createValidationException(
                UNABLE_UPDATE_UNALLOWED_ROLES,
                toCommaDelimitedString(newRoles),
                toCommaDelimitedString(allowedRolesList)));
  }

  private boolean isUserCalsExternalWorker() {
    return isCalsExternalWorker(getUser());
  }

  private boolean isUserCountyAdmin() {
    return isCountyAdmin(getUser());
  }

  private boolean isUserSuperAdmin() {
    return isSuperAdmin(getUser());
  }

  private boolean isUsersStateAdmin() {
    return isStateAdmin(getUser());
  }

  private boolean userAndAdminAreTheSameUser() {
    return getUser().getId().equals(getCurrentUserName());
  }

  private UserValidationException createValidationException(MessageCode messageCode,
      String... args) {
    return exceptionFactory.createValidationException(messageCode, args);
  }

  private AdminAuthorizationException createAuthorizationException(MessageCode messageCode,
      String... args) {
    return exceptionFactory.createAuthorizationException(messageCode, args);
  }

  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
