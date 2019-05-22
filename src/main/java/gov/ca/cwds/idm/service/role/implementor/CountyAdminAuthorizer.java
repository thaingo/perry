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

  CountyAdminAuthorizer(User user, UserUpdate userUpdate) {
    super(user, userUpdate);
  }

  @Override
  public ErrorRuleList getViewUserRules() {
    return new ErrorRuleList()
        .add(rules.adminAndUserAreInTheSameCounty(COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY,
            getUser().getId()))
        .add(rules.userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE));
  }

  @Override
  public ErrorRuleList getCreateUserRules() {
    return new ErrorRuleList()
        .add(rules.adminAndUserAreInTheSameCounty(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY,
            getUser().getCountyName()))
        .add(rules.createdUserRolesMayBe(OFFICE_ADMIN, CWS_WORKER));
  }

  @Override
  public ErrorRuleList getResendInvitationMessageRules() {
    return new ErrorRuleList()
        .add(rules.adminAndUserAreInTheSameCounty(
            COUNTY_ADMIN_CANNOT_RESEND_INVITATION_FOR_USER_FROM_OTHER_COUNTY, getUser().getId()));
  }

  @Override
  public ErrorRuleList getUpdateUserRules() {
    return new ErrorRuleList()
        .add(rules.userAndAdminAreNotTheSameUser())
        .add(rules.adminAndUserAreInTheSameCounty(COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY,
            getUser().getId()))
        .add(rules.userIsNotStateAdmin(COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN))
        .add(rules.userIsNotSuperAdmin(NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE))
        .add(rules.calsExternalWorkerRolesCanNotBeChanged())
        .add(rules.cwsWorkerRolesMayBeChangedTo(OFFICE_ADMIN, CWS_WORKER))
        .add(rules.officeAdminUserRolesMayBeChangedTo(OFFICE_ADMIN, CWS_WORKER))
        .add(rules.countyAdminUserRolesMayBeChangedTo(COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER));
  }
}
