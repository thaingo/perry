package gov.ca.cwds.idm.service.role.implementor;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.UserRolesService;

class CalsAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  CalsAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public boolean canViewUser() {
    return UserRolesService.isCalsExternalWorker(getUser());
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
