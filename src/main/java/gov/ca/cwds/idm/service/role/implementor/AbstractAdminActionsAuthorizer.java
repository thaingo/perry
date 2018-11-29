package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.service.messages.MessageCode.CALS_ADMIN_ROLES_CANNOT_BE_EDITED;
import static gov.ca.cwds.service.messages.MessageCode.CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER;

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

  protected User getUser() {
    return user;
  }

  protected void throwAuthorizationException(MessageCode messageCode, String... args) {
    throw exceptionFactory.createAuthorizationException(messageCode, args);
  }

  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
