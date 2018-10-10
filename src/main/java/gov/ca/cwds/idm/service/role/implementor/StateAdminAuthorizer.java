package gov.ca.cwds.idm.service.role.implementor;

import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;

class StateAdminAuthorizer implements AdminActionsAuthorizer {

  static final StateAdminAuthorizer INSTANCE = new StateAdminAuthorizer();

  private StateAdminAuthorizer() {
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
