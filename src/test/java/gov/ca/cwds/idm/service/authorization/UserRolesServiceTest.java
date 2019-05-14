package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.EXTERNAL_APP;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.getAdminRoles;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestAdminRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.hasCalsExternalWorkerRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.hasCountyAdminRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.hasOfficeAdminRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.hasStateAdminRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.hasSuperAdminRole;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isCountyAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isNonRacfIdCalsUser;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isOfficeAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isStateAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isSuperAdmin;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.isUserWithMainRole;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import java.util.Set;
import org.junit.Test;

public class UserRolesServiceTest {

  @Test
  public void testGetAdminRoles() {
    Set<String> adminRoles = getAdminRoles();
    assertThat(adminRoles, hasSize(4));
    assertTrue(adminRoles.contains(SUPER_ADMIN));
    assertTrue(adminRoles.contains(COUNTY_ADMIN));
    assertTrue(adminRoles.contains(STATE_ADMIN));
    assertTrue(adminRoles.contains(OFFICE_ADMIN));
  }

  @Test
  public void testIsAdmin() {
    assertFalse(isAdmin(userToken()));
    assertFalse(isAdmin(userToken(CWS_WORKER)));
    assertFalse(isAdmin(userToken(EXTERNAL_APP)));
    assertFalse(isAdmin(userToken(CALS_EXTERNAL_WORKER, CWS_WORKER)));
    assertTrue(isAdmin(userToken(CWS_WORKER, COUNTY_ADMIN)));
    assertTrue(isAdmin(userToken(STATE_ADMIN, OFFICE_ADMIN)));
    assertTrue(isAdmin(userToken(OFFICE_ADMIN)));
    assertTrue(isAdmin(userToken(STATE_ADMIN)));
  }

  @Test
  public void testIsNonRacfIdCalsUser() {
    assertFalse(isNonRacfIdCalsUser(userToken()));
    assertFalse(isNonRacfIdCalsUser((userToken(CWS_WORKER))));
    assertFalse(isNonRacfIdCalsUser(userToken(COUNTY_ADMIN)));
    assertTrue(isNonRacfIdCalsUser(userToken(CALS_EXTERNAL_WORKER)));
    assertTrue(isNonRacfIdCalsUser(userToken(OFFICE_ADMIN, CALS_EXTERNAL_WORKER)));
    assertTrue(isNonRacfIdCalsUser(userToken(CALS_EXTERNAL_WORKER, CWS_WORKER)));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetStrongestAdminRoleForIdmJob() {
    getStrongestAdminRole(userToken(EXTERNAL_APP));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetStrongestAdminRoleForNotAdmin() {
    getStrongestAdminRole(userToken());
  }

  @Test
  public void testGetStrongestAdminRole() {
    assertThat(getStrongestAdminRole(userToken(SUPER_ADMIN)), is(SUPER_ADMIN));
    assertThat(getStrongestAdminRole(userToken(STATE_ADMIN)), is(STATE_ADMIN));
    assertThat(getStrongestAdminRole(userToken(COUNTY_ADMIN)), is(COUNTY_ADMIN));
    assertThat(getStrongestAdminRole(userToken(OFFICE_ADMIN)), is(OFFICE_ADMIN));
    assertThat(getStrongestAdminRole(userToken(SUPER_ADMIN, STATE_ADMIN)),
        is(SUPER_ADMIN));
    assertThat(getStrongestAdminRole(userToken(STATE_ADMIN, COUNTY_ADMIN)),
        is(STATE_ADMIN));
    assertThat(getStrongestAdminRole(userToken(OFFICE_ADMIN, COUNTY_ADMIN)),
        is(COUNTY_ADMIN));
    assertThat(getStrongestAdminRole(userToken(OFFICE_ADMIN, STATE_ADMIN)),
        is(STATE_ADMIN));
  }

  @Test
  public void testHasStateAdminRole() {
    assertTrue(hasStateAdminRole(user(STATE_ADMIN)));
    assertTrue(hasStateAdminRole(user(STATE_ADMIN, COUNTY_ADMIN)));
    assertFalse(hasStateAdminRole(user(COUNTY_ADMIN)));
    assertFalse(hasStateAdminRole(user()));
  }

  @Test
  public void testIsStateAdmin() {
    assertTrue(isStateAdmin(user(STATE_ADMIN)));
    assertTrue(isStateAdmin(user(STATE_ADMIN, COUNTY_ADMIN)));
    assertFalse(isStateAdmin(user(STATE_ADMIN, SUPER_ADMIN)));
    assertFalse(isStateAdmin(user(COUNTY_ADMIN)));
    assertFalse(isStateAdmin(user()));
  }

  @Test
  public void testUserHasCountyAdminRole() {
    assertTrue(hasCountyAdminRole(user(COUNTY_ADMIN)));
    assertTrue(hasCountyAdminRole(user(COUNTY_ADMIN, OFFICE_ADMIN)));
    assertTrue(hasCountyAdminRole(user(STATE_ADMIN, COUNTY_ADMIN)));
    assertFalse(hasCountyAdminRole(user(STATE_ADMIN)));
    assertFalse(hasCountyAdminRole(user(OFFICE_ADMIN)));
    assertFalse(hasCountyAdminRole(user()));
  }

  @Test
  public void testUserIsCountyAdmin() {
    assertTrue(isCountyAdmin(user(COUNTY_ADMIN)));
    assertTrue(isCountyAdmin(user(COUNTY_ADMIN, OFFICE_ADMIN)));
    assertFalse(isCountyAdmin(user(STATE_ADMIN, COUNTY_ADMIN)));
    assertFalse(isCountyAdmin(user(STATE_ADMIN)));
    assertFalse(isCountyAdmin(user(OFFICE_ADMIN)));
    assertFalse(isCountyAdmin(user()));
  }

  @Test
  public void testUserHasOfficeAdminRole() {
    assertTrue(hasOfficeAdminRole(user(OFFICE_ADMIN)));
    assertTrue(hasOfficeAdminRole(user(OFFICE_ADMIN, CWS_WORKER)));
    assertTrue(hasOfficeAdminRole(user(STATE_ADMIN, OFFICE_ADMIN)));
    assertFalse(hasOfficeAdminRole(user(STATE_ADMIN)));
    assertFalse(hasOfficeAdminRole(user(COUNTY_ADMIN)));
    assertFalse(hasOfficeAdminRole(user()));
  }

  @Test
  public void testUserIsOfficeAdmin() {
    assertTrue(isOfficeAdmin(user(OFFICE_ADMIN)));
    assertTrue(isOfficeAdmin(user(CWS_WORKER, OFFICE_ADMIN)));
    assertFalse(isOfficeAdmin(user(CWS_WORKER, COUNTY_ADMIN)));
    assertFalse(isOfficeAdmin(user(STATE_ADMIN)));
    assertFalse(isOfficeAdmin(user(COUNTY_ADMIN)));
    assertFalse(isOfficeAdmin(user()));
  }

  @Test
  public void testAdminHasCountyAdminRole() {
    assertTrue(hasCountyAdminRole(userToken(COUNTY_ADMIN)));
    assertTrue(hasCountyAdminRole(userToken(COUNTY_ADMIN, OFFICE_ADMIN)));
    assertTrue(hasCountyAdminRole(userToken(STATE_ADMIN, COUNTY_ADMIN)));
    assertFalse(hasCountyAdminRole(userToken(EXTERNAL_APP)));
    assertFalse(hasCountyAdminRole(userToken()));
  }

  @Test
  public void testHasSuperAdminRole() {
    assertTrue(hasSuperAdminRole(user(SUPER_ADMIN)));
    assertTrue(hasSuperAdminRole(user(SUPER_ADMIN, STATE_ADMIN)));
    assertFalse(hasSuperAdminRole(user(COUNTY_ADMIN)));
    assertFalse(hasSuperAdminRole(user()));
  }

  @Test
  public void testIsSuperAdmin() {
    assertTrue(isSuperAdmin(user(SUPER_ADMIN)));
    assertTrue(isSuperAdmin(user(SUPER_ADMIN, STATE_ADMIN)));
    assertFalse(isSuperAdmin(user(COUNTY_ADMIN)));
    assertFalse(isSuperAdmin(user()));
  }

  @Test
  public void testCalsAdminCanView() {
    assertTrue(hasCalsExternalWorkerRole(user(CALS_EXTERNAL_WORKER)));
  }

  @Test
  public void testCalsAdminCanNotView() {
    assertFalse(hasCalsExternalWorkerRole(user(CWS_WORKER)));
  }

  @Test
  public void testIsUserWithMainRole() {

    assertTrue(isUserWithMainRole(user(SUPER_ADMIN), SUPER_ADMIN));
    assertTrue(isUserWithMainRole(user(SUPER_ADMIN, STATE_ADMIN), SUPER_ADMIN));
    assertFalse(isUserWithMainRole(user(COUNTY_ADMIN), SUPER_ADMIN));
    assertFalse(isUserWithMainRole(user(), SUPER_ADMIN));

    assertTrue(isUserWithMainRole(user(STATE_ADMIN), STATE_ADMIN));
    assertTrue(isUserWithMainRole(user(STATE_ADMIN, COUNTY_ADMIN), STATE_ADMIN));
    assertFalse(isUserWithMainRole(user(STATE_ADMIN, SUPER_ADMIN), STATE_ADMIN));
    assertFalse(isUserWithMainRole(user(COUNTY_ADMIN), STATE_ADMIN));
    assertFalse(isUserWithMainRole(user(), STATE_ADMIN));

    assertTrue(isUserWithMainRole(user(COUNTY_ADMIN), COUNTY_ADMIN));
    assertTrue(isUserWithMainRole(user(COUNTY_ADMIN, OFFICE_ADMIN), COUNTY_ADMIN));
    assertFalse(isUserWithMainRole(user(STATE_ADMIN, COUNTY_ADMIN), COUNTY_ADMIN));
    assertFalse(isUserWithMainRole(user(STATE_ADMIN), COUNTY_ADMIN));
    assertFalse(isUserWithMainRole(user(OFFICE_ADMIN), COUNTY_ADMIN));
    assertFalse(isUserWithMainRole(user(), COUNTY_ADMIN));

    assertTrue(isUserWithMainRole(user(OFFICE_ADMIN), OFFICE_ADMIN));
    assertTrue(isUserWithMainRole(user(CWS_WORKER, OFFICE_ADMIN), OFFICE_ADMIN));
    assertFalse(isUserWithMainRole(user(CWS_WORKER, COUNTY_ADMIN), OFFICE_ADMIN));
    assertFalse(isUserWithMainRole(user(STATE_ADMIN), OFFICE_ADMIN));
    assertFalse(isUserWithMainRole(user(COUNTY_ADMIN), OFFICE_ADMIN));
    assertFalse(isUserWithMainRole(user(), OFFICE_ADMIN));

    assertTrue(isUserWithMainRole(user(CWS_WORKER), CWS_WORKER));
    assertTrue(isUserWithMainRole(user(CWS_WORKER, CALS_EXTERNAL_WORKER), CWS_WORKER));
    assertFalse(isUserWithMainRole(user(CWS_WORKER, COUNTY_ADMIN), CWS_WORKER));
    assertFalse(isUserWithMainRole(user(COUNTY_ADMIN), CWS_WORKER));
    assertFalse(isUserWithMainRole(user(), CWS_WORKER));

    assertTrue(isUserWithMainRole(user(CALS_EXTERNAL_WORKER), CALS_EXTERNAL_WORKER));
    assertFalse(isUserWithMainRole(user(CWS_WORKER, CALS_EXTERNAL_WORKER), CALS_EXTERNAL_WORKER));
    assertFalse(isUserWithMainRole(user(COUNTY_ADMIN), CALS_EXTERNAL_WORKER));
    assertFalse(isUserWithMainRole(user(), CALS_EXTERNAL_WORKER));
  }

  static private UniversalUserToken userToken(String... roles) {
    UniversalUserToken token = new UniversalUserToken();
    token.setRoles(toSet(roles));
    return token;
  }

  static private User user(String... roles) {
    User user = new User();
    user.setRoles(toSet(roles));
    return user;
  }
}
