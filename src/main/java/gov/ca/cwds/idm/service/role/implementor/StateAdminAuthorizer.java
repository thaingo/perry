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
    checkUserIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Override
  public void checkCanCreateUser() {
    checkRolesAreAllowedAtCreate(COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER);
  }

  @Override
  public void checkCanResendInvitationMessage() {
    //no authorization rules to check
  }

  @Override
  public void checkCanUpdateUser(UserUpdate userUpdate) {
    checkUserIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
    checkStateAdminUserRolesAreNotEdited(userUpdate);
    checkCalsExternalWorkerRolesAreNotEdited(userUpdate);
    checkCanChangeCwsWorkerRoleTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER);
    checkCanChangeOfficeAdminUserRoleTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER);
    checkCanChangeCountyAdminUserRoleTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER);
  }
}
