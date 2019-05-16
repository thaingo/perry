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
import static gov.ca.cwds.service.messages.MessageCode.CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.service.messages.MessageCode.STATE_ADMIN_ROLES_CANNOT_BE_EDITED;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_UPDATE_UNALLOWED_ROLES;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;
import static gov.ca.cwds.util.Utils.toCommaDelimitedString;
import static java.util.Arrays.asList;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.exception.IdmException;
import gov.ca.cwds.idm.exception.UserValidationException;
import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.rule.ErrorRule;
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

  @Override
  public final boolean canUpdateUser(UserUpdate userUpdate) {
    return false;
  }

  protected User getUser() {
    return user;
  }

  protected final void checkUserIsNotSuperAdmin(MessageCode errorCode) {
    if (isSuperAdmin(getUser())) {
      throw createAuthorizationException(errorCode, getStrongestAdminRole(getCurrentUser()),
          getUser().getId());
    }
  }

  protected void checkUserIsNotStateAdmin(MessageCode errorCode) {
    new ErrorRule(){
      @Override
      public boolean hasError() {
        return userIsStateAdmin();
      }
      @Override
      public IdmException createException() {
        return createAuthorizationException(errorCode, getUser().getId());
      }
    }.check();
  }

  protected void checkUserIsNotCountyAdmin(MessageCode errorCode) {
    new ErrorRule(){
      @Override
      public boolean hasError() {
        return userIsCountyAdmin();
      }
      @Override
      public IdmException createException() {
        return createAuthorizationException(errorCode, getUser().getId());
      }
    }.check();
  }

  private boolean userIsCountyAdmin() {
    return isCountyAdmin(getUser());
  }

  private boolean userIsStateAdmin() {
    return isStateAdmin(getUser());
  }

  protected final void checkUserisNotCalsExternalWorker(MessageCode errorCode, String... args) {
    if (isCalsExternalWorker(getUser())) {
      throw createAuthorizationException(errorCode, args);
    }
  }

  protected final void checkAdminAndUserInTheSameCounty(MessageCode errorCode, String... args) {
    if (!isAdminInTheSameCountyAsUser()) {
      throw createAuthorizationException(errorCode, args);
    }
  }

  protected final void checkAdminAndUserInTheSameOffice(MessageCode errorCode) {
    new ErrorRule() {
      @Override
      public boolean hasError() {
        return adminAndUserAreInDifferentOffices();
      }

      @Override
      public IdmException createException() {
        return createAuthorizationException(errorCode, getUser().getId());
      }
    }.check();
//    if (adminAndUserAreInDifferentOffices()) {
//      throw createAuthorizationException(errorCode, getUser().getId());
//    }
  }

  private boolean adminAndUserAreInDifferentOffices() {
    return !isAdminInTheSameOfficeAsUser();
  }

  protected final void checkStateAdminUserRolesAreNotEdited(UserUpdate userUpdate) {
    checkRolesAreNotEdited(STATE_ADMIN, userUpdate, STATE_ADMIN_ROLES_CANNOT_BE_EDITED);
  }

  protected final void checkCalsExternalWorkerRolesAreNotEdited(UserUpdate userUpdate) {
    checkRolesAreNotEdited(CALS_EXTERNAL_WORKER, userUpdate, CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER);
  }

  private void checkRolesAreNotEdited(String userMainRole, UserUpdate userUpdate, MessageCode errorCode) {
    if(userUpdate.getRoles() == null){
      return;
    }
    if (isUserWithMainRole(getUser(), userMainRole) && !Utils.toSet(userMainRole).equals(userUpdate.getRoles())) {
      throw createAuthorizationException(errorCode, getUser().getId());
    }
  }

  protected final boolean isAdminInTheSameCountyAsUser() {
    String userCountyName = getUser().getCountyName();
    String adminCountyName = getCurrentUserCountyName();
    return userCountyName != null && userCountyName.equals(adminCountyName);
  }

  protected final boolean isAdminInTheSameOfficeAsUser() {
    String userOfficeId = getUser().getOfficeId();
    Set<String> adminOfficeIds = getCurrentUserOfficeIds();
    return userOfficeId != null && adminOfficeIds != null && adminOfficeIds.contains(userOfficeId);
  }

  protected final void checkRolesAreAllowedAtCreate(String... allowedRoles) {
    Collection<String> roles = getUser().getRoles();
    List<String> allowedRolesList = asList(allowedRoles);

    if (roles != null && (!allowedRolesList.containsAll(roles))) {
      throw createValidationException(
          UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES,
          toCommaDelimitedString(roles),
          toCommaDelimitedString(allowedRolesList));
    }
  }

  protected final void checkCanChangeCwsWorkerRoleTo(UserUpdate userUpdate, String... allowedRoles) {
    checkUserCanChangeRoleOnlyTo(CWS_WORKER, userUpdate, allowedRoles);
  }

  protected final void checkCanChangeOfficeAdminUserRoleTo(UserUpdate userUpdate, String... allowedRoles) {
    checkUserCanChangeRoleOnlyTo(OFFICE_ADMIN, userUpdate, allowedRoles);
  }

  protected final void checkCanChangeCountyAdminUserRoleTo(UserUpdate userUpdate, String... allowedRoles) {
    checkUserCanChangeRoleOnlyTo(COUNTY_ADMIN, userUpdate, allowedRoles);
  }

  protected final void checkCanChangeStateAdminUserRoleTo(UserUpdate userUpdate, String... allowedRoles) {
    checkUserCanChangeRoleOnlyTo(STATE_ADMIN, userUpdate, allowedRoles);
  }

  private void checkUserCanChangeRoleOnlyTo(String userCurrentRole, UserUpdate userUpdate,
      String... allowedRoles) {

    Set<String> newRoles = userUpdate.getRoles();
    List<String> allowedRolesList = asList(allowedRoles);

    if (newRoles != null
        && isUserWithMainRole(getUser(), userCurrentRole)
        && (!allowedRolesList.containsAll(newRoles))) {

        throw createValidationException(
            UNABLE_UPDATE_UNALLOWED_ROLES,
            toCommaDelimitedString(newRoles),
            toCommaDelimitedString(allowedRolesList));
    }
  }

  private UserValidationException createValidationException(MessageCode messageCode, String... args) {
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
