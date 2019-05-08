package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestAdminRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCalsExternalWorker;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCountyAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isSuperAdmin;
import static gov.ca.cwds.idm.service.role.implementor.AuthorizationUtils.isPrincipalInTheSameCountyWith;
import static gov.ca.cwds.service.messages.MessageCode.CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.service.messages.MessageCode;
import java.util.Set;
import liquibase.util.StringUtils;

/**
 * Created by Alexander Serbin on 11/2/2018
 */
public abstract class AbstractAdminActionsAuthorizer implements AdminActionsAuthorizer {

  private User user;

  private ExceptionFactory exceptionFactory;

  AbstractAdminActionsAuthorizer(User user) {
    this.user = user;
  }

  @Override
  public void checkCanEditRoles() {
    checkUserIsNotCalsExternalWorker(CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER, user.getId());
  }

  @Override
  public void checkCanEditPermissions() {
    //no authorization rules to check
  }

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

  protected final void checkUserIsNotCalsExternalWorker(MessageCode errorMessage, String... args) {
    if (isCalsExternalWorker(getUser())) {
      throwAuthorizationException(errorMessage, args);
    }
  }

  protected final void checkAdminAndUserInTheSameCounty(MessageCode errorMessage, String... args) {
    if (isNotEmpty(getUser().getCountyName()) && !isPrincipalInTheSameCountyWith(getUser())) {
      throwAuthorizationException(errorMessage, args);
    }
  }

  protected final void checkAdminAndUserInTheSameOffice(MessageCode errorCode) {
    if (!isAdminInTheSameOfficeAsUser()) {
      throwAuthorizationException(errorCode, getUser().getId());
    }
  }

  private boolean isAdminInTheSameOfficeAsUser() {
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
