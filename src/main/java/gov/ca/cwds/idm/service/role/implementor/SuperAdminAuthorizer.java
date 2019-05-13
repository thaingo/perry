package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import java.util.List;

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
    checkRolesAreAllowedAtCreate(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER,
        CALS_EXTERNAL_WORKER);
  }

  @Override
  public void checkCanUpdateUser(UserUpdate userUpdate) {
    checkCanChangeCwsWorkerRoleTo(userUpdate,
        STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER);
    checkCanChangeOfficeAdminUserRoleTo(userUpdate,
        STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER);
    checkCanChangeCountyAdminUserRoleTo(userUpdate,
        STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER);
    checkCanChangeStateAdminUserRoleTo(userUpdate,
        STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER);
  }

  @Override
  public void checkCanResendInvitationMessage() {
    //no authorization rules to check
  }

  @Override
  public List<String> getMaxAllowedUserRolesAtUpdate() {
    return unmodifiableList(
        asList(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER,
            CALS_EXTERNAL_WORKER));
  }
}
