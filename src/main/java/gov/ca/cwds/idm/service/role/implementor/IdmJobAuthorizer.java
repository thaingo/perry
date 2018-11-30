package gov.ca.cwds.idm.service.role.implementor;

import gov.ca.cwds.idm.dto.User;

class IdmJobAuthorizer extends AbstractAdminActionsAuthorizer {

  IdmJobAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    //no authorization rules to check
  }

  @Override
  public void checkCanCreateUser() {
    unsufficientRoleError();
  }

  @Override
  public void checkCanUpdateUser() {
    unsufficientRoleError();
  }

  @Override
  public void checkCanResendInvitationMessage() {
    unsufficientRoleError();
  }

  @Override
  public void checkCanEditRoles() {
    unsufficientRoleError();
  }
}
