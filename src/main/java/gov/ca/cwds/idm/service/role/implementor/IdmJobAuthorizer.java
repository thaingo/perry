package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.service.messages.MessageCode.IDM_JOB_CANNOT_UPDATE_USER;

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
    throwAuthorizationException(IDM_JOB_CANNOT_UPDATE_USER);
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
