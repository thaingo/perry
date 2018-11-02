package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCountyAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.idm.service.role.implementor.AuthorizationUtils.isPrincipalInTheSameCountyWith;

import gov.ca.cwds.idm.dto.User;

class CountyAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  CountyAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public boolean canViewUser() {
    return isPrincipalInTheSameCountyWith(getUser());
  }

  @Override
  public boolean canCreateUser() {
    return isPrincipalInTheSameCountyWith(getUser());
  }

  @Override
  public boolean canUpdateUser() {
    return isPrincipalInTheSameCountyWith(getUser()) && !isStateAdmin(getUser());
  }

  @Override
  public boolean canResendInvitationMessage() {
    return isPrincipalInTheSameCountyWith(getUser());
  }

  @Override
  public boolean canEditRoles() {
    return super.canEditRoles() && (!isStateAdmin(getUser()) && !isCountyAdmin(getUser()));
  }

}
