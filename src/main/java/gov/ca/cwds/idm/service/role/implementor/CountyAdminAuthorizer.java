package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.rule.ErrorRuleList;

class CountyAdminAuthorizer extends AbstractAdminActionsAuthorizer {

  CountyAdminAuthorizer(User user) {
    super(user);
  }

  @Override
  public ErrorRuleList getViewUserRules() {
    return new ErrorRuleList()
        .rule(adminAndUserAreInTheSameCounty(COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY,
            getUser().getId()))
        .rule(userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE));
  }

  @Override
  public ErrorRuleList getCreateUserRules() {
    return new ErrorRuleList()
        .rule(adminAndUserAreInTheSameCounty(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY,
            getUser().getCountyName()))
        .rule(createdUserRolesMayBe(OFFICE_ADMIN, CWS_WORKER));
  }

  @Override
  public ErrorRuleList getResendInvitationMessageRules() {
    return new ErrorRuleList()
        .rule(adminAndUserAreInTheSameCounty(
            COUNTY_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_COUNTY, getUser().getId()));
  }

  @Override
  public ErrorRuleList getUpdateUserRules(UserUpdate userUpdate) {
    return new ErrorRuleList()
        .rule(userAndAdminAreNotTheSameUser())
        .rule(adminAndUserAreInTheSameCounty(COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY,
            getUser().getId()))
        .rule(userIsNotStateAdmin(COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN))
        .rule(userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE))
        .rule(calsExternalWorkerRolesAreNotChanged(userUpdate))
        .rule(cwsWorkerRolesMayBeChangedTo(userUpdate, OFFICE_ADMIN, CWS_WORKER))
        .rule(officeAdminUserRolesMayBeChangedTo(userUpdate, OFFICE_ADMIN, CWS_WORKER))
        .rule(
            countyAdminUserRolesMayBeChangedTo(userUpdate, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER));
  }
}
