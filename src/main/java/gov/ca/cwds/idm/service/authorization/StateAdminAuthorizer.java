package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.User;

class StateAdminAuthorizer implements AdminActionsAuthorizer {

  private final User user;

  public StateAdminAuthorizer(User user) {
    this.user = user;
  }

  @Override
  public boolean canViewUser() {
    return true;
  }

  @Override
  public boolean canCreateUser() {
    return true;
  }

  @Override
  public boolean canUpdateUser() {
    return true;
  }

  @Override
  public boolean canResendInvitationMessage() {
    return true;
  }

}
