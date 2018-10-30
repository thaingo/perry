package gov.ca.cwds.idm.service.role.implementor;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;
import gov.ca.cwds.idm.service.authorization.UserRolesService;

class CalsAdminAuthorizer implements AdminActionsAuthorizer {

  private User user;

  CalsAdminAuthorizer(User user) {
    this.user = user;
  }

  @Override
  public boolean canViewUser() {
    return UserRolesService.isCalsExternalWorker(user);
  }

  @Override
  public boolean canCreateUser() {
    return false;
  }

  @Override
  public boolean canUpdateUser() {
    return false;
  }

  @Override
  public boolean canResendInvitationMessage() {
    return false;
  }

  @Override
  public boolean canEditRoles() {
    return false;
  }

}
