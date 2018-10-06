package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.idm.service.authorization.AuthorizationUtils.isPrincipalInTheSameCountyWith;

import gov.ca.cwds.idm.dto.User;

class CountyAdminAuthorizer implements AdminActionsAuthorizer {

  private final User user;

  public CountyAdminAuthorizer(User user) {
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
