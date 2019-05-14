package gov.ca.cwds.idm.event;

import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserFirstName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserLastName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;
import static gov.ca.cwds.util.UserNameFormatter.formatUserFullName;

import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.UserRolesService;

public abstract class AdminCausedChangeLogEvent extends UserChangeLogEvent {

  AdminCausedChangeLogEvent(User user) {
    super(user);
    setUserLogin(getCurrentUserName());
    setAdminName(formatUserFullName(getCurrentUserLastName(), getCurrentUserFirstName()));
    String adminRoles = Roles
        .getRoleNameById(UserRolesService.getStrongestAdminRole(getCurrentUser()));
    setAdminRole(adminRoles);
  }
}
