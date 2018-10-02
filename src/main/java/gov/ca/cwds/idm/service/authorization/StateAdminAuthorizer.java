package gov.ca.cwds.idm.service.authorization;

class StateAdminAuthorizer implements AdminActionsAuthorizer {

  public static final StateAdminAuthorizer INSTANCE = new StateAdminAuthorizer();

  private StateAdminAuthorizer() {
  }

  @Override
  public boolean canFindUser() {
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
