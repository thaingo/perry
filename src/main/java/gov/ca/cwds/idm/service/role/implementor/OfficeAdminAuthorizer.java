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
  public ErrorRuleList getViewUserRules() {
    return new ErrorRuleList()
        .rule(adminAndUserAreInTheSameCounty(
            OFFICE_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY, getUser().getId()))
        .rule(userIsNotCalsExternalWorker(
            OFFICE_ADMIN_CANNOT_VIEW_USERS_WITH_CALS_EXTERNAL_WORKER_ROLE))
        .rule(userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE));
  }

  @Override
  public ErrorRuleList getCreateUserRules() {
    return new ErrorRuleList()
        .rule(adminAndUserAreInTheSameOffice(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE))
        .rule(createdUserRolesMayBe(CWS_WORKER));
  }

  @Override
  public ErrorRuleList getUpdateUserRules(UserUpdate userUpdate) {
    return new ErrorRuleList()
        .rule(userAndAdminAreNotTheSameUser())
        .rule(adminAndUserAreInTheSameOffice(OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE))
        .rule(userIsNotCountyAdmin(OFFICE_ADMIN_CANNOT_UPDATE_COUNTY_ADMIN))
        .rule(userIsNotStateAdmin(OFFICE_ADMIN_CANNOT_UPDATE_STATE_ADMIN))
        .rule(userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE))
        .rule(calsExternalWorkerRolesAreNotChanged(userUpdate))
        .rule(cwsWorkerRolesMayBeChangedTo(userUpdate, CWS_WORKER))
        .rule(officeAdminUserRolesMayBeChangedTo(userUpdate, OFFICE_ADMIN, CWS_WORKER));
  }

  @Override
  public ErrorRuleList getResendInvitationMessageRules() {
    return new ErrorRuleList()
        .rule(adminAndUserAreInTheSameOffice(
            OFFICE_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_OFFICE));
  }
}
