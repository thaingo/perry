package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.service.messages.MessageCode.STATE_ADMIN_ROLES_CANNOT_BE_EDITED;

import gov.ca.cwds.idm.dto.User;

class StateAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  StateAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
  }

  @Override
  public void checkCanCreateUser() {
  }

  @Override
  public void checkCanUpdateUser() {
  }

  @Override
  public void checkCanResendInvitationMessage() {
  }

  @Override
  public void checkCanEditRoles() {
    super.checkCanEditRoles();

    if(isStateAdmin(getUser())) {
      throwAuthorizationException(STATE_ADMIN_ROLES_CANNOT_BE_EDITED, getUser().getId());
    }
  }
}
