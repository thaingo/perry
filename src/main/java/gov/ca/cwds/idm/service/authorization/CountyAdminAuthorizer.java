package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.idm.service.authorization.AuthorizationUtils.principalInTheSameCountyWith;

import gov.ca.cwds.idm.dto.User;

class CountyAdminAuthorizer implements ActionsAuthorizer {

  private final User user;

  public CountyAdminAuthorizer(User user) {
    this.user = user;
  }

  @Override
  public boolean canFindUser() {
    return principalInTheSameCountyWith(user);
  }

  @Override
  public boolean canCreateUser() {
    return principalInTheSameCountyWith(user);
  }

  @Override
  public boolean canUpdateUser() {
    return principalInTheSameCountyWith(user);
  }

  @Override
  public boolean canResendInvitationMessage() {
    return principalInTheSameCountyWith(user);
  }

}
