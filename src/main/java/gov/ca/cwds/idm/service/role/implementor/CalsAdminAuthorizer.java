package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCalsExternalWorker;
import static gov.ca.cwds.service.messages.MessageCode.CALS_ADMIN_CANNOT_VIEW_NON_CALS_USER;

import gov.ca.cwds.idm.dto.User;

class CalsAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  CalsAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    checkUserIsCalsExternalWorker();
    checkUserIsNotSuperAdmin(CALS_ADMIN);
  }

  private void checkUserIsCalsExternalWorker() {
    if (!isCalsExternalWorker(getUser())) {
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
}
