package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.AuthorizationTestHelper;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "gov.ca.cwds.util.CurrentAuthenticatedUserUtil")
public class AuthorizeCompleteUpdateServiceTest {

  private AuthorizeCompleteUpdateServiceImpl service;

  @Before
  public void before() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    service = new AuthorizeCompleteUpdateServiceImpl();
    service.setAdminRoleImplementorFactory(new AdminRoleImplementorFactory());
  }

  @Test
  public void testStateAdminAssign() {
    testAdminCanNotAssign(admin(STATE_ADMIN), user(STATE_ADMIN));
    testAdminCanAssign(admin(STATE_ADMIN), user(COUNTY_ADMIN));
    testAdminCanAssign(admin(STATE_ADMIN), user(OFFICE_ADMIN));
    testAdminCanAssign(admin(STATE_ADMIN), user(CWS_WORKER));
  }

  @Test
  public void testCountyAdminAssign() {
    testAdminCanNotAssign(admin(COUNTY_ADMIN), user(STATE_ADMIN));
    testAdminCanNotAssign(admin(COUNTY_ADMIN), user(COUNTY_ADMIN));
    testAdminCanAssign(admin(COUNTY_ADMIN), user(OFFICE_ADMIN));
    testAdminCanAssign(admin(COUNTY_ADMIN), user(CWS_WORKER));
  }

  @Test
  public void testOfficeAdminAssignStateAdmin() {
    testAdminCanNotAssign(admin(OFFICE_ADMIN), user(STATE_ADMIN));
    testAdminCanNotAssign(admin(OFFICE_ADMIN), user(COUNTY_ADMIN));
    testAdminCanNotAssign(admin(OFFICE_ADMIN), user(OFFICE_ADMIN));
    testAdminCanAssign(admin(OFFICE_ADMIN), user(CWS_WORKER));
  }

  private void testAdminCanAssign(UniversalUserToken admin, User user) {
    testAdminAssign(admin, user, true);
  }

  private void testAdminCanNotAssign(UniversalUserToken admin, User user) {
    testAdminAssign(admin, user, false);
  }

  private void testAdminAssign(UniversalUserToken admin, User user, boolean expected) {
    when(getCurrentUser()).thenReturn(admin);
    assertEquals(expected, service.canCompleteUpdateUser(user));
  }

  private static UniversalUserToken admin(String role) {
    UniversalUserToken admin =
        AuthorizationTestHelper.admin(toSet(role), "Yolo", toSet("Yolo_1"));
    admin.setUserId("adminId");
    return admin;
  }

  private static User user(String role) {
    User user = AuthorizationTestHelper.user(toSet(role), "Madera", "Madera_1");
    user.setId("userId");
    return user;
  }
}
