package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
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

class SuperAdminRoleImplementor implements AdminRoleImplementor {

  @Override
  public AbstractAdminActionsAuthorizer getAdminActionsAuthorizer(User user) {
    return new SuperAdminAuthorizer(user);
  }

  @Override
  public List<String> getPossibleUserRoles() {
    return unmodifiableList(
        asList(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CALS_ADMIN,
            CWS_WORKER, CALS_EXTERNAL_WORKER));
  }
}
