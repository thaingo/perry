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

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;

class CountyAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  CountyAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    adminAndUserAreInTheSameCounty(COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY,
        getUser().getId()).check();
    userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE).check();
  }

  @Override
  public void checkCanCreateUser() {
    adminAndUserAreInTheSameCounty(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY, getUser().getCountyName()).check();
    createdUserRolesMayBe(OFFICE_ADMIN, CWS_WORKER).check();
  }

  @Override
  public void checkCanResendInvitationMessage() {
    adminAndUserAreInTheSameCounty(
        COUNTY_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_COUNTY, getUser().getId()).check();
  }

  @Override
  public void checkCanUpdateUser(UserUpdate userUpdate) {
    adminAndUserAreInTheSameCounty(COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY, getUser().getId()).check();
    userIsNotStateAdmin(COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN).check();
    userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE).check();
    calsExternalWorkerRolesAreNotChanged(userUpdate).check();
    cwsWorkerRolesMayBeChangedTo(userUpdate, OFFICE_ADMIN, CWS_WORKER).check();
    officeAdminUserRolesMayBeChangedTo(userUpdate, OFFICE_ADMIN, CWS_WORKER).check();
    countyAdminUserRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER).check();
  }
}
