package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.service.authorization.UserRolesService.isAdmin;
import static gov.ca.cwds.idm.service.role.implementor.AuthorizationUtils.isPrincipalInTheSameCountyWith;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import java.util.Set;

class OfficeAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  OfficeAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    if(!isPrincipalInTheSameCountyWith(getUser())) {
      throwAuthorizationException(OFFICE_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY, getUser().getId());
    }
    if(UserRolesService.isCalsExternalWorker(getUser())) {
      throwAuthorizationException(OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE, getUser().getId());
    }
    if(UserRolesService.isCalsAdmin(getUser())) {
      throwAuthorizationException(OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_ADMIN_ROLE, getUser().getId());
    }
  }

  @Override
  public void checkCanCreateUser() {
    if(!isAdminInTheSameOfficeAsUser()) {
      throwAuthorizationException(
          NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE, getUser().getCountyName());
    }
  }

  @Override
  public void checkCanUpdateUser() {
   if(!isAdminInTheSameOfficeAsUser()) {
     throwAuthorizationException(
         OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE, getUser().getId());
   }

   if(isAdmin(getUser())) {
     throwAuthorizationException(
         OFFICE_ADMIN_CANNOT_UPDATE_ADMIN, getUser().getId());
   }
  }

  @Override
  public void checkCanResendInvitationMessage() {
    if (!isAdminInTheSameOfficeAsUser()) {
      throwAuthorizationException(
          OFFICE_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_OFFICE, getUser().getId());
    }
  }

  private boolean isAdminInTheSameOfficeAsUser() {
    String userOfficeId = getUser().getOfficeId();
    Set<String> adminOfficeIds = getCurrentUserOfficeIds();
    return userOfficeId != null && adminOfficeIds != null && adminOfficeIds.contains(userOfficeId);
  }
}
