package gov.ca.cwds.idm.service.role.implementor;

import static java.util.Collections.unmodifiableList;

import gov.ca.cwds.idm.dto.User;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander Serbin on 10/9/2018
 */
class CalsAdminRoleImplementor extends AbstractAdminRoleImplementor {

  @Override
  public AbstractAdminActionsAuthorizer getAdminActionsAuthorizer(User user) {
    return new CalsAdminAuthorizer(user);
  }

  @Override
  public List<String> getPossibleUserRoles() {
    return unmodifiableList(Collections.emptyList());
  }

}
