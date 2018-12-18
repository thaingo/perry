package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isSuperAdmin;
import static gov.ca.cwds.idm.service.role.implementor.AuthorizationUtils.isPrincipalInTheSameCountyWith;
import static gov.ca.cwds.service.messages.MessageCode.CALS_ADMIN_ROLES_CANNOT_BE_EDITED;
import static gov.ca.cwds.service.messages.MessageCode.CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.ROLE_IS_UNSUFFICIENT_FOR_OPERATION;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.service.messages.MessageCode;

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
    if(UserRolesService.isCalsExternalWorker(user)) {
      throwAuthorizationException(CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER, user.getId());
    }

    if(UserRolesService.isCalsAdmin(user)) {
      throwAuthorizationException(CALS_ADMIN_ROLES_CANNOT_BE_EDITED, user.getId());
    }
  }

  @Override
  public void checkCanEditPermissions() {
    //no authorization rules to check
  }

  protected User getUser() {
    return user;
  }

  protected final void throwAuthorizationException(MessageCode messageCode, String... args) {
    throw exceptionFactory.createAuthorizationException(messageCode, args);
  }

  protected final void unsufficientRoleError() {
    throwAuthorizationException(ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }

  protected final void checkUserIsNotSuperAdmin(MessageCode errorCode, String roleName) {
    if (isSuperAdmin(getUser())) {
      throwAuthorizationException(errorCode, roleName, getUser().getId());
    }
  }

  protected final void checkAdminAndUserInTheSameCounty(MessageCode errorMessage) {
    if (!isPrincipalInTheSameCountyWith(getUser())) {
      throwAuthorizationException(errorMessage, getUser().getId());
    }
  }

  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
