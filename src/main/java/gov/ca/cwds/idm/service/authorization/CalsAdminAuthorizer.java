package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.User;

class CalsAdminAuthorizer implements AdminActionsAuthorizer {

  private User user;

  public CalsAdminAuthorizer(User user) {
    this.user = user;
  }

  @Override
  public boolean canFindUser() {
    return UserRolesService.isCalsExternalWorker(user);
  }

  @Override
  public boolean canCreateUser() {
    return false;
  }

  @Override
  public boolean canUpdateUser() {
    return false;
  }

  @Override
  public boolean canResendInvitationMessage() {
    return false;
  }

}
