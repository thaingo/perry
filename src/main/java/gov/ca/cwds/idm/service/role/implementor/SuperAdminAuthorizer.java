package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;

class SuperAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  SuperAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    //no authorization rules to check
  }

  @Override
  public void checkCanCreateUser() {
    createdUserRolesMayBe(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER,
        CALS_EXTERNAL_WORKER).check();
  }

  @Override
  public void checkCanUpdateUser(UserUpdate userUpdate) {
    cwsWorkerRolesMayBeChangedTo(userUpdate,
        SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER).check();
    officeAdminUserRolesMayBeChangedTo(userUpdate,
        SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER).check();
    countyAdminUserRolesMayBeChangedTo(userUpdate,
        SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER).check();
    stateAdminUserRolesMayBeChangedTo(userUpdate,
        SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER).check();
  }

  @Override
  public void checkCanResendInvitationMessage() {
    //no authorization rules to check
  }
}
