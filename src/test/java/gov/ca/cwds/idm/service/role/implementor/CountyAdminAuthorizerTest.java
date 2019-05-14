package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.superAdmin;
import static gov.ca.cwds.idm.util.TestHelper.user;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.Utils.toSet;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import org.junit.Before;
import org.junit.Test;

public class CountyAdminAuthorizerTest extends BaseAuthorizerTest {

  @Before
  public void mockCountyAdmin() {
    when(getCurrentUser()).thenReturn(admin(toSet(COUNTY_ADMIN),
        ADMIN_COUNTY, toSet(ADMIN_OFFICE)));
    when(getCurrentUserCountyName()).thenReturn(ADMIN_COUNTY);
  }

  @Override
  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return new CountyAdminAuthorizer(user);
  }

  @Test
  public void canNotViewSuperAdmin() {
    canNotView(superAdmin(), NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Test
  public void canNotUpdateSuperAdmin() {
    canNotUpdateWithAuthorizationError(superAdmin(), NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Test
  public void canCreateCwsWorkerInSameCounty() {
    canCreate(user(toSet(CWS_WORKER), ADMIN_COUNTY, "someOffice"));
  }

  @Test
  public void canCreateOfficeAdminInSameCounty() {
    canCreate(user(toSet(OFFICE_ADMIN), ADMIN_COUNTY, "someOffice"));
  }

  public void canNotCreateCwsWorkerInOtherCounty() {
    canNotCreateWithAuthorizationError(user(toSet(CWS_WORKER), "otherCounty", "otherOffice"),
        NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY);
  }

  public void canNotCreateOfficeAdminInOtherCounty() {
    canNotCreateWithAuthorizationError(user(toSet(OFFICE_ADMIN), "otherCounty", "otherOffice"),
        NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY);
  }

  @Test
  public void canNotCreateStateAdminInSameCounty() {
    canNotCreateInSameCounty(STATE_ADMIN);
  }

  @Test
  public void canNotCreateSuperAdminInSameCounty() {
    canNotCreateInSameCounty(SUPER_ADMIN);
  }

  @Test
  public void canNotCreateCalsExternalWorkerInSameCounty() {
    canNotCreateInSameCounty(CALS_EXTERNAL_WORKER);
  }

  private void canNotCreateInSameCounty(String userRole) {
    canNotCreateWithValidationError(user(toSet(userRole), ADMIN_COUNTY, "someOffice"),
        UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES);
  }
}