package gov.ca.cwds.idm.service.role.implementor;

import gov.ca.cwds.idm.dto.User;

class SuperAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  SuperAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    //no authorization rules to check
  }

  @Override
  public void checkCanCreateUser() {
    //no authorization rules to check
  }

  @Override
  public void checkCanUpdateUser() {
    //no authorization rules to check
  }

  @Override
  public void checkCanResendInvitationMessage() {
    //no authorization rules to check
  }

  @Override
  public void checkCanEditRoles() {
    //no authorization rules to check
  }
}
