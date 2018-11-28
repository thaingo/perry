package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCountyAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.idm.service.role.implementor.AuthorizationUtils.isPrincipalInTheSameCountyWith;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY;

import gov.ca.cwds.idm.dto.User;

class CountyAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  CountyAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    if(!isPrincipalInTheSameCountyWith(getUser())) {
      throwAuthorizationException(COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY, getUser().getId());
    }
  }

  @Override
  public boolean canCreateUser() {
    return isPrincipalInTheSameCountyWith(getUser());
  }

  @Override
  public void checkCanUpdateUser() {
    if(!isPrincipalInTheSameCountyWith(getUser())) {
      throwAuthorizationException(
          COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY, getUser().getId());
    }

    if(isStateAdmin(getUser())) {
      throwAuthorizationException(
          COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN, getUser().getId());
    }
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
