package gov.ca.cwds.idm.service.role.implementor;

import gov.ca.cwds.idm.dto.User;

class IdmJobAuthorizer extends AbstractAdminActionsAuthorizer {

  IdmJobAuthorizer(User user) {
    super(user);
  }

  @Override
  public void canViewUser() {
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
