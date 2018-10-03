package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.idm.service.authorization.AuthorizationUtils.isPrincipalInTheSameCountyWith;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCountyAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isOfficeAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getAdminOfficeIds;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import java.util.Set;

class OfficeAdminAuthorizer implements AdminActionsAuthorizer {

  private final User user;

  public OfficeAdminAuthorizer(User user) {
    this.user = user;
  }

  @Override
  public boolean canFindUser() {
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
    return isAdminInTheSameOfficeAsUser() &&
        !isStateAdmin(user) &&
        !isCountyAdmin(user) &&
        !isOfficeAdmin(user);
  }

  @Override
  public boolean canResendInvitationMessage() {
    return isAdminInTheSameOfficeAsUser();
  }

  private boolean userIsStateAdminFromOtherOffice() {
    return isStateAdmin(user) && !isAdminInTheSameOfficeAsUser();
  }

  private boolean userIsCountyAdminFromOtherOffice() {
    return isCountyAdmin(user) && !isAdminInTheSameOfficeAsUser();
  }

  private boolean isAdminInTheSameOfficeAsUser() {
    UniversalUserToken admin = getCurrentUser();
    String userOfficeId = user.getOfficeId();
    Set<String> adminOfficeIds = getAdminOfficeIds(admin);
    return userOfficeId != null && adminOfficeIds != null && adminOfficeIds.contains(userOfficeId);
  }

}
