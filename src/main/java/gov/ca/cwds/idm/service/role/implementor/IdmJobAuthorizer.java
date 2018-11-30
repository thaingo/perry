package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.service.messages.MessageCode.ROLE_IS_UNSUFFICIENT_FOR_OPERATION;

import gov.ca.cwds.idm.dto.User;

@SuppressWarnings({"common-java:DuplicatedBlocks"})
//it's not a duplication but super class method invocation where common code is extracted
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
