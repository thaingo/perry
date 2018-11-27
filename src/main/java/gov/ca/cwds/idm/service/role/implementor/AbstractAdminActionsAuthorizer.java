package gov.ca.cwds.idm.service.role.implementor;

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

  protected User getUser() {
    return user;
  }

  protected void throwAuthorizationException(MessageCode messageCode, String... args) {
    throw exceptionFactory.createAuthorizationException(messageCode, args);
  }

  @Override
  public boolean canEditRoles() {
    return !UserRolesService.isCalsExternalWorker(user) && !UserRolesService.isCalsAdmin(user);
  }

  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
