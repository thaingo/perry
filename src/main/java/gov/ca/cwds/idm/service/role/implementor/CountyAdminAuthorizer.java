package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCountyAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.idm.service.role.implementor.AuthorizationUtils.isPrincipalInTheSameCountyWith;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_EDIT_ROLES_OF_OTHER_COUNTY_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.STATE_ADMIN_ROLES_CANNOT_BE_EDITED;

import gov.ca.cwds.idm.dto.User;

class CountyAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  CountyAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    checkUserInTheSameCounty();
    checkUserIsNotSuperAdmin(COUNTY_ADMIN);
  }

  private void checkUserInTheSameCounty() {
    if (!isPrincipalInTheSameCountyWith(getUser())) {
      throwAuthorizationException(COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY,
          getUser().getId());
    }
  }

  @Override
  public void checkCanCreateUser() {
    if(!isPrincipalInTheSameCountyWith(getUser())) {
      throwAuthorizationException(
          NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY, getUser().getCountyName());
    }
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
  public void checkCanResendInvitationMessage() {
    if (!isPrincipalInTheSameCountyWith(getUser())) {
      throwAuthorizationException(
          COUNTY_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_COUNTY, getUser().getId());
    }
  }

  @Override
  public void checkCanEditRoles() {
   super.checkCanEditRoles();

    if (isStateAdmin(getUser())) {
      throwAuthorizationException(STATE_ADMIN_ROLES_CANNOT_BE_EDITED, getUser().getId());
    }

    if (isCountyAdmin(getUser())) {
      throwAuthorizationException(
          COUNTY_ADMIN_CANNOT_EDIT_ROLES_OF_OTHER_COUNTY_ADMIN, getUser().getId());
    }
  }
}
