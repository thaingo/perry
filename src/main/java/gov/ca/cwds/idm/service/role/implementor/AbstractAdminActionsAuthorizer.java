package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestAdminRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCalsExternalWorker;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCountyAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isSuperAdmin;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.service.messages.MessageCode;
import java.util.Set;

public abstract class AbstractAdminActionsAuthorizer implements AdminActionsAuthorizer {

  private User user;

  private ExceptionFactory exceptionFactory;

  AbstractAdminActionsAuthorizer(User user) {
    this.user = user;
  }

//  @Override
//  public void checkCanEditRoles() {
//    checkUserisNotCalsExternalWorker(CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER, user.getId());
//  }

  protected User getUser() {
    return user;
  }

  protected final void checkUserIsNotSuperAdmin(MessageCode errorCode) {
    if (isSuperAdmin(getUser())) {
      throwAuthorizationException(errorCode, getStrongestAdminRole(getCurrentUser()), getUser().getId());
    }
  }

  protected void checkUserIsNotStateAdmin(MessageCode errorCode) {
    if (isStateAdmin(getUser())) {
      throwAuthorizationException(errorCode, getUser().getId());
    }
  }

  protected void checkUserIsNotCountyAdmin(MessageCode errorCode) {
    if (isCountyAdmin(getUser())) {
      throwAuthorizationException(errorCode, getUser().getId());
    }
  }

  protected final void checkUserisNotCalsExternalWorker(MessageCode errorMessage, String... args) {
    if (isCalsExternalWorker(getUser())) {
      throwAuthorizationException(errorMessage, args);
    }
  }

  protected final void checkAdminAndUserInTheSameCounty(MessageCode errorMessage, String... args) {
    if (!isAdminInTheSameCountyAsUser()) {
      throwAuthorizationException(errorMessage, args);
    }
  }

  protected final void checkAdminAndUserInTheSameOffice(MessageCode errorCode) {
    if (!isAdminInTheSameOfficeAsUser()) {
      throwAuthorizationException(errorCode, getUser().getId());
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

  private final void throwAuthorizationException(MessageCode messageCode, String... args) {
    throw exceptionFactory.createAuthorizationException(messageCode, args);
  }

  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
