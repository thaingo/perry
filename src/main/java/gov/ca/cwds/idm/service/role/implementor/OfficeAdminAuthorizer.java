package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isOfficeAdmin;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_COUNTY_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY;
import static java.util.Collections.unmodifiableList;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class OfficeAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  OfficeAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    checkAdminAndUserInTheSameCounty(
        OFFICE_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY, getUser().getId());
    checkUserisNotCalsExternalWorker(
        OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE, getUser().getId());
    checkUserIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Override
  public void checkCanCreateUser() {
    checkAdminAndUserInTheSameOffice(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE);
  }

  @Override
  public void checkCanUpdateUser(UserUpdate userUpdate) {
    checkAdminAndUserInTheSameOffice(OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE);
    checkUserIsNotCountyAdmin(OFFICE_ADMIN_CANNOT_UPDATE_COUNTY_ADMIN);
    checkUserIsNotStateAdmin(OFFICE_ADMIN_CANNOT_UPDATE_STATE_ADMIN);
    checkUserIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
    checkCalsExternalWorkerRolesAreNotEdited(userUpdate);
  }

  @Override
  public List<String> getMaxAllowedUserRolesAtUpdate() {
    User user = getUser();

    if (isOfficeAdmin(user) && isAdminInTheSameOfficeAsUser()) {
      return unmodifiableList(Arrays.asList(OFFICE_ADMIN, CWS_WORKER));
    } else {
      return Collections.singletonList(CWS_WORKER);
    }
  }

  @Override
  public void checkCanResendInvitationMessage() {
    checkAdminAndUserInTheSameOffice(
        OFFICE_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_OFFICE);
  }

  @Override
  public List<String> getMaxAllowedUserRolesAtCreate() {
    return Collections.singletonList(CWS_WORKER);
  }
}
