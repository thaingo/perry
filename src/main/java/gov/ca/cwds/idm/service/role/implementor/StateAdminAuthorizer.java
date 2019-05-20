package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.rule.ErrorRuleList;

class StateAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  StateAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    new ErrorRuleList()
        .addRule(userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE))
        .check();
  }

  @Override
  public void checkCanCreateUser() {
    new ErrorRuleList()
        .addRule(createdUserRolesMayBe(COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER))
        .check();
  }

  @Override
  public void checkCanResendInvitationMessage() {
    new ErrorRuleList().check();
  }

  @Override
  public void checkCanUpdateUser(UserUpdate userUpdate) {
    new ErrorRuleList()
        .addRule(userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE))
        .addRule(stateAdminUserRolesAreNotChanged(userUpdate))
        .addRule(calsExternalWorkerRolesAreNotChanged(userUpdate))
        .addRule(cwsWorkerRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER))
        .addRule(
            officeAdminUserRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER))
        .addRule(
            countyAdminUserRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER))
        .check();
  }
}
