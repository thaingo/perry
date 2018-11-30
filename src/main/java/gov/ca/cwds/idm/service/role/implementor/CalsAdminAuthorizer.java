package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.service.messages.MessageCode.CALS_ADMIN_CANNOT_VIEW_NON_CALS_USER;

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
