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
  @SuppressWarnings({"common-java:DuplicatedBlocks"})
  public void checkCanCreateUser() {
    unsufficientRoleError();
  }

  @Override
  @SuppressWarnings({"common-java:DuplicatedBlocks"})
  public void checkCanUpdateUser() {
    unsufficientRoleError();
  }

  @Override
  @SuppressWarnings({"common-java:DuplicatedBlocks"})
  public void checkCanResendInvitationMessage() {
    unsufficientRoleError();
  }

  @Override
  @SuppressWarnings({"common-java:DuplicatedBlocks"})
  public void checkCanEditRoles() {
    unsufficientRoleError();
  }
}
