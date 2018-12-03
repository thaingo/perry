package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.DictionaryProvider;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Created by Alexander Serbin on 10/9/2018
 */
@Service
@Profile("idm")
public class AdminRoleImplementorFactory {

  @Autowired
  private DictionaryProvider dictionaryProvider;

  public AdminRoleImplementor createAdminRoleImplementor() {
    AbstractAdminRoleImplementor implementor;

    switch (UserRolesService.getStrongestAdminRole(getCurrentUser())) {
      case STATE_ADMIN:
        implementor = new StateAdminRoleImplementor();
        break;
      case COUNTY_ADMIN:
        implementor = new CountyAdminRoleImplementor();
        break;
      case OFFICE_ADMIN:
        implementor = new OfficeAdminRoleImplementor();
        break;
      case CALS_ADMIN:
        implementor = new CalsAdminRoleImplementor();
        break;
      default:
        throw new IllegalStateException();
    }
    implementor.setDictionaryProvider(dictionaryProvider);
    return implementor;
  }

  public AbstractAdminActionsAuthorizer getAdminActionsAuthorizer(User user) {
    return createAdminRoleImplementor().getAdminActionsAuthorizer(user);
  }

  public List<String> getPossibleUserRoles() {
    return createAdminRoleImplementor().getPossibleUserRoles();
  }

  public List<String> getPossibleUserPermissions(boolean isRacfidUser) {
    return createAdminRoleImplementor().getPossibleUserPermissions(isRacfidUser);
  }
}
