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
  public ErrorRuleList getViewUserRules() {
    return new ErrorRuleList()
        .addRule(userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE));
  }

  @Override
  public ErrorRuleList getCreateUserRules() {
    return new ErrorRuleList()
        .addRule(createdUserRolesMayBe(COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER));
  }

  @Override
  public ErrorRuleList getResendInvitationMessageRules() {
    return new ErrorRuleList();
  }

  @Override
  public ErrorRuleList getUpdateUserRules(UserUpdate userUpdate) {
    return new ErrorRuleList()
        .addRule(userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE))
        .addRule(stateAdminUserRolesAreNotChanged(userUpdate))
        .addRule(calsExternalWorkerRolesAreNotChanged(userUpdate))
        .addRule(cwsWorkerRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER))
        .addRule(
            officeAdminUserRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER))
        .addRule(
            countyAdminUserRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER));
  }
}
