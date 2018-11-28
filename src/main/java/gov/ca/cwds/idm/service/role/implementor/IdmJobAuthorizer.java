package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.service.messages.MessageCode.ROLE_IS_UNSUFFICIENT_FOR_OPERATION;

import gov.ca.cwds.idm.dto.User;

class IdmJobAuthorizer extends AbstractAdminActionsAuthorizer {

  IdmJobAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
  }

  @Override
  public boolean canCreateUser() {
    return false;
  }

  @Override
  public void checkCanUpdateUser() {
    throwAuthorizationException(ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }

  @Override
  public void checkCanResendInvitationMessage() {
    throwAuthorizationException(ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }

  @Override
  public boolean canEditRoles() {
    return false;
  }

}
