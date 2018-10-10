package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class AuthorizeCompleteUpdateServiceImpl implements AuthorizeCompleteUpdateService {

  private AdminRoleImplementorFactory adminRoleImplementorFactory;

  @Override
  public boolean canCompleteUpdateUser(User newUser) {
    Collection<String> newUserRoles = newUser.getRoles();
    Collection<String> allowedRoles = getAllowedRoles();
    return allowedRoles.containsAll(newUserRoles);
  }

  private Collection<String> getAllowedRoles() {
    return adminRoleImplementorFactory.getPossibleUserRoles();
  }

  @Autowired
  public void setAdminRoleImplementorFactory(
      AdminRoleImplementorFactory adminRoleImplementorFactory) {
    this.adminRoleImplementorFactory = adminRoleImplementorFactory;
  }
}
