package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.service.role.implementor.AuthorizationUtils.isPrincipalInTheSameCountyWith;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;

class CountyAdminAuthorizer implements AdminActionsAuthorizer {

  private final User user;

  CountyAdminAuthorizer(User user) {
    this.user = user;
  }

  @Override
  public boolean canViewUser() {
    return isPrincipalInTheSameCountyWith(user);
  }

  @Override
  public boolean canCreateUser() {
    return isPrincipalInTheSameCountyWith(user);
  }

  @Override
  public boolean canUpdateUser() {
    return isPrincipalInTheSameCountyWith(user);
  }

  @Override
  public boolean canResendInvitationMessage() {
    return isPrincipalInTheSameCountyWith(user);
  }

}
