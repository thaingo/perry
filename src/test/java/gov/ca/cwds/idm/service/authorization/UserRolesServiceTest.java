package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.EXTERNAL_APP;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.getAdminRoles;
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
    assertThat(adminRoles, hasSize(5));
    assertTrue(adminRoles.contains(SUPER_ADMIN));
    assertTrue(adminRoles.contains(COUNTY_ADMIN));
    assertTrue(adminRoles.contains(STATE_ADMIN));
    assertTrue(adminRoles.contains(OFFICE_ADMIN));
  }

  @Test
  public void testIsAdmin() {
    assertFalse(UserRolesService.isAdmin(userToken()));
    assertFalse(UserRolesService.isAdmin(userToken(CWS_WORKER)));
    assertFalse(UserRolesService.isAdmin(userToken(EXTERNAL_APP)));
    assertFalse(UserRolesService.isAdmin(userToken(CALS_EXTERNAL_WORKER, CWS_WORKER)));
    assertTrue(UserRolesService.isAdmin(userToken(CWS_WORKER, COUNTY_ADMIN)));
    assertTrue(UserRolesService.isAdmin(userToken(STATE_ADMIN, OFFICE_ADMIN)));
    assertTrue(UserRolesService.isAdmin(userToken(OFFICE_ADMIN)));
    assertTrue(UserRolesService.isAdmin(userToken(CALS_ADMIN)));
    assertTrue(UserRolesService.isAdmin(userToken(STATE_ADMIN)));
  }

  @Test
  public void testIsNonRacfIdCalsUser() {
    assertFalse(UserRolesService.isNonRacfIdCalsUser(userToken()));
    assertFalse(UserRolesService.isNonRacfIdCalsUser((userToken(CWS_WORKER))));
    assertFalse(UserRolesService.isNonRacfIdCalsUser(userToken(COUNTY_ADMIN)));
    assertTrue(UserRolesService.isNonRacfIdCalsUser(userToken(CALS_EXTERNAL_WORKER)));
    assertTrue(UserRolesService.isNonRacfIdCalsUser(userToken(OFFICE_ADMIN, CALS_EXTERNAL_WORKER)));
    assertTrue(UserRolesService.isNonRacfIdCalsUser(userToken(CALS_EXTERNAL_WORKER, CWS_WORKER)));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetStrongestAdminRoleForIdmJob() {
    UserRolesService.getStrongestAdminRole(userToken(EXTERNAL_APP));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetStrongestAdminRoleForNotAdmin() {
    UserRolesService.getStrongestAdminRole(userToken());
  }

  @Test
  public void testGetStrongestAdminRole() {
    assertThat(UserRolesService.getStrongestAdminRole(userToken(SUPER_ADMIN)), is(SUPER_ADMIN));
    assertThat(UserRolesService.getStrongestAdminRole(userToken(STATE_ADMIN)), is(STATE_ADMIN));
    assertThat(UserRolesService.getStrongestAdminRole(userToken(COUNTY_ADMIN)), is(COUNTY_ADMIN));
    assertThat(UserRolesService.getStrongestAdminRole(userToken(OFFICE_ADMIN)), is(OFFICE_ADMIN));
    assertThat(UserRolesService.getStrongestAdminRole(userToken(CALS_ADMIN)), is(CALS_ADMIN));
    assertThat(UserRolesService.getStrongestAdminRole(userToken(SUPER_ADMIN, STATE_ADMIN)),
        is(SUPER_ADMIN));
    assertThat(UserRolesService.getStrongestAdminRole(userToken(STATE_ADMIN, COUNTY_ADMIN)),
        is(STATE_ADMIN));
    assertThat(UserRolesService.getStrongestAdminRole(userToken(OFFICE_ADMIN, COUNTY_ADMIN)),
        is(COUNTY_ADMIN));
    assertThat(UserRolesService.getStrongestAdminRole(userToken(OFFICE_ADMIN, STATE_ADMIN)),
        is(STATE_ADMIN));
    assertThat(UserRolesService.getStrongestAdminRole(userToken(OFFICE_ADMIN, CALS_ADMIN)),
        is(OFFICE_ADMIN));
  }

  @Test
  public void testIsCalsAdmin() {
    assertTrue(UserRolesService.isCalsAdmin(userToken(CALS_ADMIN)));
    assertTrue(UserRolesService.isCalsAdmin(userToken(CALS_ADMIN, OFFICE_ADMIN)));
    assertTrue(UserRolesService.isCalsAdmin(userToken(STATE_ADMIN, CALS_ADMIN)));
    assertFalse(UserRolesService.isCalsAdmin(userToken(EXTERNAL_APP)));
    assertFalse(UserRolesService.isCalsAdmin(userToken(OFFICE_ADMIN, COUNTY_ADMIN)));
    assertFalse(UserRolesService.isCalsAdmin(userToken()));
  }

  @Test
  public void testIsStateAdmin() {
    assertTrue(UserRolesService.isStateAdmin(user(STATE_ADMIN)));
    assertTrue(UserRolesService.isStateAdmin(user(STATE_ADMIN, COUNTY_ADMIN)));
    assertFalse(UserRolesService.isStateAdmin(user(COUNTY_ADMIN)));
    assertFalse(UserRolesService.isStateAdmin(user()));
  }

  @Test
  public void testUserIsCountyAdmin() {
    assertTrue(UserRolesService.isCountyAdmin(user(COUNTY_ADMIN)));
    assertTrue(UserRolesService.isCountyAdmin(user(COUNTY_ADMIN, OFFICE_ADMIN)));
    assertTrue(UserRolesService.isCountyAdmin(user(STATE_ADMIN, COUNTY_ADMIN)));
    assertFalse(UserRolesService.isCountyAdmin(user(STATE_ADMIN)));
    assertFalse(UserRolesService.isCountyAdmin(user(OFFICE_ADMIN)));
    assertFalse(UserRolesService.isCountyAdmin(user()));
  }

  @Test
  public void testAdminIsCountyAdmin() {
    assertTrue(UserRolesService.isCountyAdmin(userToken(COUNTY_ADMIN)));
    assertTrue(UserRolesService.isCountyAdmin(userToken(COUNTY_ADMIN, OFFICE_ADMIN)));
    assertTrue(UserRolesService.isCountyAdmin(userToken(STATE_ADMIN, COUNTY_ADMIN)));
    assertFalse(UserRolesService.isCountyAdmin(userToken(EXTERNAL_APP)));
    assertFalse(UserRolesService.isCountyAdmin(userToken(OFFICE_ADMIN, CALS_ADMIN)));
    assertFalse(UserRolesService.isCountyAdmin(userToken()));
  }

  @Test
  public void testIsSuperAdmin() {
    assertTrue(UserRolesService.isSuperAdmin(user(SUPER_ADMIN)));
    assertTrue(UserRolesService.isSuperAdmin(user(SUPER_ADMIN, STATE_ADMIN)));
    assertFalse(UserRolesService.isSuperAdmin(user(COUNTY_ADMIN)));
    assertFalse(UserRolesService.isSuperAdmin(user()));
  }

  @Test
  public void testCalsAdminCanView() {
    User user = user(CALS_EXTERNAL_WORKER);
    assertTrue(UserRolesService.isCalsExternalWorker(user));
  }

  @Test
  public void testCalsAdminCanNotView() {
    User user = user(CWS_WORKER);
    assertFalse(UserRolesService.isCalsExternalWorker(user));
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
