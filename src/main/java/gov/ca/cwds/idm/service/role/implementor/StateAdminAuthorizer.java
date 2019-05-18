package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;

class StateAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  StateAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE).check();
  }

  @Override
  public void checkCanCreateUser() {
    createdUserRolesMayBe(COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER).check();
  }

  @Override
  public void checkCanResendInvitationMessage() {
    //no authorization rules to check
  }

  @Override
  public void checkCanUpdateUser(UserUpdate userUpdate) {
    userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE).check();
    stateAdminUserRolesAreNotChanged(userUpdate).check();
    calsExternalWorkerRolesAreNotChanged(userUpdate).check();
    cwsWorkerRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER).check();
    officeAdminUserRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER).check();
    countyAdminUserRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER).check();
  }
}
