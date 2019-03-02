package gov.ca.cwds.idm.event;

import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserFullName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.UserRolesService;

public abstract class AdminCausedChangeLogEvent extends UserChangeLogEvent {

  AdminCausedChangeLogEvent(User user) {
    super(user);
    setUserLogin(getCurrentUserName());
    setAdminName(getCurrentUserFullName());
    String adminRoles = Roles
        .getRoleNameById(UserRolesService.getStrongestAdminRole(getCurrentUser()));
    setAdminRole(adminRoles);
  }
}
