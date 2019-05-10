package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static java.util.Collections.unmodifiableList;

import gov.ca.cwds.idm.dto.User;
import java.util.Arrays;
import java.util.List;

class CountyAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  CountyAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    checkAdminAndUserInTheSameCounty(COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY,
        getUser().getId());
    checkUserIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Override
  public void checkCanCreateUser() {
    checkAdminAndUserInTheSameCounty(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY, getUser().getCountyName());
  }

  @Override
  public void checkCanUpdateUser() {
    checkAdminAndUserInTheSameCounty(COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY, getUser().getId());
    checkUserIsNotStateAdmin(COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN);
    checkUserIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Override
  public void checkCanResendInvitationMessage() {
    checkAdminAndUserInTheSameCounty(
        COUNTY_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_COUNTY, getUser().getId());
  }

  @Override
  public List<String> getPossibleUserRolesAtCreate() {
    return unmodifiableList(Arrays.asList(OFFICE_ADMIN, CWS_WORKER));
  }

  @Override
  public List<String> getPossibleUserRolesAtUpdate() {
    return unmodifiableList(Arrays.asList(COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER));
  }


}
