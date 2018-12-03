package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static java.util.Collections.unmodifiableList;

import gov.ca.cwds.idm.dto.User;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alexander Serbin on 10/9/2018
 */
class CountyAdminRoleImplementor extends AbstractAdminRoleImplementor {

  @Override
  public AbstractAdminActionsAuthorizer getAdminActionsAuthorizer(User user) {
    return new CountyAdminAuthorizer(user);
  }

  @Override
  public List<String> getPossibleUserRoles() {
    return unmodifiableList(Arrays.asList(OFFICE_ADMIN, CWS_WORKER));
  }

}
