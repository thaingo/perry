package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.service.messages.MessageCode.CALS_ADMIN_CANNOT_VIEW_NON_CALS_USER;
import static gov.ca.cwds.service.messages.MessageCode.ROLE_IS_UNSUFFICIENT_FOR_OPERATION;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.UserRolesService;

class CalsAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  CalsAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    if (!UserRolesService.isCalsExternalWorker(getUser())) {
      throwAuthorizationException(CALS_ADMIN_CANNOT_VIEW_NON_CALS_USER, getUser().getId());
    }
  }

  @Override
  public void checkCanCreateUser() {
    throwAuthorizationException(ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
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
  public void checkCanEditRoles() {
    throwAuthorizationException(ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }
}
