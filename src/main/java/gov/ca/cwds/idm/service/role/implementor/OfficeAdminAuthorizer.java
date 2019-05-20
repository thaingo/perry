package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_COUNTY_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.rule.ErrorRuleList;

class OfficeAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  OfficeAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public void checkCanViewUser() {
    new ErrorRuleList()
        .addRule(adminAndUserAreInTheSameCounty(
            OFFICE_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY, getUser().getId()))
        .addRule(userIsNotCalsExternalWorker(
            OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE))
        .addRule(userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE))
        .check();
  }

  @Override
  public void checkCanCreateUser() {
    new ErrorRuleList()
        .addRule(adminAndUserAreInTheSameOffice(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE))
        .addRule(createdUserRolesMayBe(CWS_WORKER))
        .check();
  }

  @Override
  public void checkCanUpdateUser(UserUpdate userUpdate) {
    new ErrorRuleList()
        .addRule(adminAndUserAreInTheSameOffice(OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE))
        .addRule(userIsNotCountyAdmin(OFFICE_ADMIN_CANNOT_UPDATE_COUNTY_ADMIN))
        .addRule(userIsNotStateAdmin(OFFICE_ADMIN_CANNOT_UPDATE_STATE_ADMIN))
        .addRule(userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE))
        .addRule(calsExternalWorkerRolesAreNotChanged(userUpdate))
        .addRule(cwsWorkerRolesMayBeChangedTo(userUpdate, CWS_WORKER))
        .addRule(officeAdminUserRolesMayBeChangedTo(userUpdate, OFFICE_ADMIN, CWS_WORKER))
        .check();
  }

  @Override
  public void checkCanResendInvitationMessage() {
    new ErrorRuleList()
        .addRule(adminAndUserAreInTheSameOffice(
            OFFICE_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_OFFICE))
        .check();
  }
}
