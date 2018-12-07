package gov.ca.cwds.idm.service.role.implementor;

import gov.ca.cwds.idm.dto.User;
import java.util.List;

/**
 * Created by Alexander Serbin on 10/9/2018
 */
public interface AdminRoleImplementor {

  AbstractAdminActionsAuthorizer getAdminActionsAuthorizer(User user);

  List<String> getPossibleUserRoles();
}
