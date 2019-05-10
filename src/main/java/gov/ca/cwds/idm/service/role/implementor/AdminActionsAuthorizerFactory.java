package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class AdminActionsAuthorizerFactory {

  public AbstractAdminActionsAuthorizer getAdminActionsAuthorizer(User user) {

    switch (UserRolesService.getStrongestAdminRole(getCurrentUser())) {
      case SUPER_ADMIN:
        return new SuperAdminAuthorizer(user);
      case STATE_ADMIN:
        return new StateAdminAuthorizer(user);
      case COUNTY_ADMIN:
        return new CountyAdminAuthorizer(user);
      case OFFICE_ADMIN:
        return new OfficeAdminAuthorizer(user);
      default:
        throw new IllegalStateException();
    }
  }
}
