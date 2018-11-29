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

  private void unsufficientRoleError() {
    throwAuthorizationException(ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }
}
