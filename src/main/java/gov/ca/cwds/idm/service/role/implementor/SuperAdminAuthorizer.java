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
  public void checkCanViewUser() {
    new ErrorRuleList().check();
  }

  @Override
  public void checkCanCreateUser() {
    new ErrorRuleList()
        .addRule(
            createdUserRolesMayBe(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER,
                CALS_EXTERNAL_WORKER))
        .check();
  }

  @Override
  public void checkCanUpdateUser(UserUpdate userUpdate) {
    new ErrorRuleList()
        .addRule(cwsWorkerRolesMayBeChangedTo(userUpdate,
            SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER))
        .addRule(officeAdminUserRolesMayBeChangedTo(userUpdate,
            SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER))
        .addRule(countyAdminUserRolesMayBeChangedTo(userUpdate,
            SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER))
        .addRule(stateAdminUserRolesMayBeChangedTo(userUpdate,
            SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER))
        .check();
  }

  @Override
  public void checkCanResendInvitationMessage() {
    new ErrorRuleList().check();
  }
}
