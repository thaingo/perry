package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.service.authorization.UserRolesService.isAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCountyAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.idm.service.role.implementor.AuthorizationUtils.isPrincipalInTheSameCountyWith;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.AdminActionsAuthorizer;
import java.util.Set;

class OfficeAdminAuthorizer implements AdminActionsAuthorizer {

  private final User user;

  OfficeAdminAuthorizer(User user) {
    this.user = user;
  }

  @Override
  public boolean canViewUser() {
    return isPrincipalInTheSameCountyWith(user)
        && !userIsStateAdminFromOtherOffice()
        && !userIsCountyAdminFromOtherOffice();
  }

  @Override
  public boolean canCreateUser() {
    return isAdminInTheSameOfficeAsUser();
  }

  @Override
  public boolean canUpdateUser() {
    return isAdminInTheSameOfficeAsUser() && !isAdmin(user);
  }

  @Override
  public boolean canResendInvitationMessage() {
    return isAdminInTheSameOfficeAsUser();
  }

  @Override
  public boolean canEditRoles() {
    //There is no requirements at this moment
    return true;
  }

  private boolean userIsStateAdminFromOtherOffice() {
    return isStateAdmin(user) && !isAdminInTheSameOfficeAsUser();
  }

  private boolean userIsCountyAdminFromOtherOffice() {
    return isCountyAdmin(user) && !isAdminInTheSameOfficeAsUser();
  }

  private boolean isAdminInTheSameOfficeAsUser() {
    String userOfficeId = user.getOfficeId();
    Set<String> adminOfficeIds = getCurrentUserOfficeIds();
    return userOfficeId != null && adminOfficeIds != null && adminOfficeIds.contains(userOfficeId);
  }

}
