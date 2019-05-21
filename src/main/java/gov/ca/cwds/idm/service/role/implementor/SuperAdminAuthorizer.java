package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.rule.ErrorRuleList;

class SuperAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  SuperAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public ErrorRuleList getViewUserRules() {
    return new ErrorRuleList();
  }

  @Override
  public ErrorRuleList getCreateUserRules() {
    return new ErrorRuleList()
        .rule(
            createdUserRolesMayBe(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER,
                CALS_EXTERNAL_WORKER));
  }

  @Override
  public ErrorRuleList getUpdateUserRules(UserUpdate userUpdate) {
    return new ErrorRuleList()
        .rule(userAndAdminAreNotTheSameUser())
        .rule(cwsWorkerRolesMayBeChangedTo(userUpdate,
            SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER))
        .rule(officeAdminUserRolesMayBeChangedTo(userUpdate,
            SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER))
        .rule(countyAdminUserRolesMayBeChangedTo(userUpdate,
            SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER))
        .rule(stateAdminUserRolesMayBeChangedTo(userUpdate,
            SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER,
            CALS_EXTERNAL_WORKER));
  }

  @Override
  public ErrorRuleList getResendInvitationMessageRules() {
    return new ErrorRuleList();
  }
}
