package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Created by Alexander Serbin on 10/9/2018
 */
@Service
@Profile("idm")
public class AdminRoleImplementorFactory {

  public AdminRoleImplementor createAdminRoleImplementor() {
    switch (UserRolesService.getStrongestAdminRole(getCurrentUser())) {
      case STATE_ADMIN:
        return new StateAdminRoleImplementor();
      case COUNTY_ADMIN:
        return new CountyAdminRoleImplementor();
      case OFFICE_ADMIN:
        return new OfficeAdminRoleImplementor();
      case CALS_ADMIN:
        return new CalsAdminRoleImplementor();
      default:
        throw new IllegalStateException();
    }
  }

  public AdminActionsAuthorizer getAdminActionsAuthorizer(User user) {
    return createAdminRoleImplementor().getAdminActionsAuthorizer(user);
  }

  public List<String> getPossibleUserRoles() {
    return createAdminRoleImplementor().getPossibleUserRoles();
  }

}
