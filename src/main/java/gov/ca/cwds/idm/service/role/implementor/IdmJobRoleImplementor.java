package gov.ca.cwds.idm.service.role.implementor;

import gov.ca.cwds.idm.dto.User;
import java.util.Collections;
import java.util.List;

class IdmJobRoleImplementor implements AdminRoleImplementor {

  @Override
  public AbstractAdminActionsAuthorizer getAdminActionsAuthorizer(User user) {
    return new IdmJobAuthorizer(user);
  }

  @Override
  public List<String> getPossibleUserRoles() {
    return Collections.emptyList();
  }
}
