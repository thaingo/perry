package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCalsExternalWorker;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import gov.ca.cwds.idm.dto.User;
import java.util.Collections;
import java.util.List;

class StateAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  StateAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    checkUserIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Override
  public void checkCanCreateUser() {
    //no authorization rules to check
  }

  @Override
  public void checkCanResendInvitationMessage() {
    //no authorization rules to check
  }

  @Override
  public void checkCanUpdateUser() {
    checkUserIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Override
  public List<String> getMaxAllowedUserRolesAtUpdate() {
    User user = getUser();

    if (isStateAdmin(user)) {
      return Collections.singletonList(STATE_ADMIN);
    } else if (isCalsExternalWorker(user)) {
      return Collections.singletonList(CALS_EXTERNAL_WORKER);
    } else {
      return unmodifiableList(asList(COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER));
    }
  }

  @Override
  public List<String> getMaxAllowedUserRolesAtCreate() {
    return unmodifiableList(asList(COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER));
  }

//  @Override
//  public void checkCanEditRoles() {
//    super.checkCanEditRoles();
//    checkUserIsNotStateAdmin(STATE_ADMIN_ROLES_CANNOT_BE_EDITED);
//  }
}
