package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;

import gov.ca.cwds.idm.dto.User;

class StateAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  StateAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
  }

  @Override
  public boolean canCreateUser() {
    return true;
  }

  @Override
  public void checkCanUpdateUser() {
  }

  @Override
  public boolean canResendInvitationMessage() {
    return true;
  }

  @Override
  public boolean canEditRoles() {
    return super.canEditRoles() && !isStateAdmin(getUser());
  }

}
