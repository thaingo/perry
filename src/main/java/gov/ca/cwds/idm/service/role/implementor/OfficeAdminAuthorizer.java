package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.service.authorization.UserRolesService.isAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCountyAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.idm.service.role.implementor.AuthorizationUtils.isPrincipalInTheSameCountyWith;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;

import gov.ca.cwds.idm.dto.User;
import java.util.Set;

class OfficeAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  OfficeAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public boolean canViewUser() {
    return isPrincipalInTheSameCountyWith(getUser())
        && !userIsStateAdminFromOtherOffice()
        && !userIsCountyAdminFromOtherOffice();
  }

  @Override
  public boolean canCreateUser() {
    return isAdminInTheSameOfficeAsUser();
  }

  @Override
  public boolean canUpdateUser() {
    return isAdminInTheSameOfficeAsUser() && !isAdmin(getUser());
  }

  @Override
  public boolean canResendInvitationMessage() {
    return isAdminInTheSameOfficeAsUser();
  }

  private boolean userIsStateAdminFromOtherOffice() {
    return isStateAdmin(getUser()) && !isAdminInTheSameOfficeAsUser();
  }

  private boolean userIsCountyAdminFromOtherOffice() {
    return isCountyAdmin(getUser()) && !isAdminInTheSameOfficeAsUser();
  }

  private boolean isAdminInTheSameOfficeAsUser() {
    String userOfficeId = getUser().getOfficeId();
    Set<String> adminOfficeIds = getCurrentUserOfficeIds();
    return userOfficeId != null && adminOfficeIds != null && adminOfficeIds.contains(userOfficeId);
  }

}
