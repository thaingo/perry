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
    //no authorization rules to check
  }

  @Override
  public void checkCanUpdateUser() {
    //no authorization rules to check
  }

  @Override
  public void checkCanResendInvitationMessage() {
    //no authorization rules to check
  }

  @Override
  public List<String> getPossibleUserRolesAtCreate() {
    return unmodifiableList(
        asList(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER,
            CALS_EXTERNAL_WORKER));
  }

  @Override
  public List<String> getPossibleUserRolesAtUpdate() {
    return unmodifiableList(
        asList(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER,
            CALS_EXTERNAL_WORKER));
  }

  @Override
  public void checkCanEditRoles() {
    //no authorization rules to check
  }
}
