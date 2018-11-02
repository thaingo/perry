package gov.ca.cwds.idm.service.role.implementor;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;
import gov.ca.cwds.idm.service.authorization.UserRolesService;

/**
 * Created by Alexander Serbin on 11/2/2018
 */
public abstract class AbstractAdminActionsAuthorizer implements AdminActionsAuthorizer {

  private User user;

  AbstractAdminActionsAuthorizer(User user) {
    this.user = user;
  }

  protected User getUser() {
    return user;
  }

  @Override
  public boolean canEditRoles() {
    return !UserRolesService.isCalsExternalWorker(user) && !UserRolesService.isCalsAdmin(user);
  }
}
