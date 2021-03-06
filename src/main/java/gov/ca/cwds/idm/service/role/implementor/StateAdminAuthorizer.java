package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.STATE_ADMIN_ROLES_CANNOT_BE_EDITED;

import gov.ca.cwds.idm.dto.User;

class StateAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  StateAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    checkUserIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Override
  public void checkCanCreateUser() {
    //no authorization rules to check
  }

  @Override
  public void checkCanUpdateUser() {
    checkUserIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Override
  public void checkCanResendInvitationMessage() {
    //no authorization rules to check
  }

  @Override
  public void checkCanEditRoles() {
    super.checkCanEditRoles();
    checkUserIsNotStateAdmin(STATE_ADMIN_ROLES_CANNOT_BE_EDITED);
  }
}
