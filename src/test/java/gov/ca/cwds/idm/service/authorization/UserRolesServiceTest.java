package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.IDM_JOB;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.getAdminRoles;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;

public class UserRolesServiceTest {

  @Test
  public void testGetAdminRoles() {
    Set<String> adminRoles = getAdminRoles();
    assertThat(adminRoles, hasSize(4));
    assertTrue(adminRoles.contains(COUNTY_ADMIN));
    assertTrue(adminRoles.contains(STATE_ADMIN));
    assertTrue(adminRoles.contains(OFFICE_ADMIN));
  }

  @Test
  public void testIsAdmin() {
    assertFalse(UserRolesService.isAdmin(userToken()));
    assertFalse(UserRolesService.isAdmin(userToken(CWS_WORKER)));
    assertFalse(UserRolesService.isAdmin(userToken(IDM_JOB)));
    assertFalse(UserRolesService.isAdmin(userToken(CALS_EXTERNAL_WORKER, CWS_WORKER)));
    assertTrue(UserRolesService.isAdmin(userToken(CWS_WORKER, COUNTY_ADMIN)));
    assertTrue(UserRolesService.isAdmin(userToken(STATE_ADMIN, OFFICE_ADMIN)));
    assertTrue(UserRolesService.isAdmin(userToken(OFFICE_ADMIN)));
    assertTrue(UserRolesService.isAdmin(userToken(CALS_ADMIN)));
  }

  @Test
  public void testIsCwsAdmin() {
    assertFalse(UserRolesService.isCwsAdmin(userToken()));
    assertFalse(UserRolesService.isCwsAdmin(userToken(CWS_WORKER)));
    assertFalse(UserRolesService.isCwsAdmin(userToken(IDM_JOB)));
    assertFalse(UserRolesService.isCwsAdmin(userToken(CALS_EXTERNAL_WORKER, CWS_WORKER)));
    assertTrue(UserRolesService.isCwsAdmin(userToken(CWS_WORKER, COUNTY_ADMIN)));
    assertTrue(UserRolesService.isCwsAdmin(userToken(STATE_ADMIN, OFFICE_ADMIN)));
    assertTrue(UserRolesService.isCwsAdmin(userToken(OFFICE_ADMIN)));
    assertFalse(UserRolesService.isCwsAdmin(userToken(CALS_ADMIN)));
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

  @Test
  public void testGetStrongestAdminRoleForIdmJob() {
    assertThat(UserRolesService.getStrongestCwsRole(userToken(IDM_JOB)), is(Optional.empty()));
  }

  @Test
  public void testGetStrongestAdminRoleForNotAdmin() {
    assertThat(UserRolesService.getStrongestCwsRole(userToken()), is(Optional.empty()));
  }

  @Test
  public void testGetStrongestCwsRole() {
    assertThat(UserRolesService.getStrongestCwsRole(userToken(STATE_ADMIN)), is(Optional.of(STATE_ADMIN)));
    assertThat(UserRolesService.getStrongestCwsRole(userToken(COUNTY_ADMIN)), is(Optional.of(COUNTY_ADMIN)));
    assertThat(UserRolesService.getStrongestCwsRole(userToken(OFFICE_ADMIN)), is(Optional.of(OFFICE_ADMIN)));
    assertThat(UserRolesService.getStrongestCwsRole(userToken(CALS_ADMIN)), is(Optional.empty()));
    assertThat(UserRolesService.getStrongestCwsRole(userToken(STATE_ADMIN, COUNTY_ADMIN)),
        is(Optional.of(STATE_ADMIN)));
    assertThat(UserRolesService.getStrongestCwsRole(userToken(OFFICE_ADMIN, COUNTY_ADMIN)),
        is(Optional.of(COUNTY_ADMIN)));
    assertThat(UserRolesService.getStrongestCwsRole(userToken(OFFICE_ADMIN, STATE_ADMIN)),
        is(Optional.of(STATE_ADMIN)));
    assertThat(UserRolesService.getStrongestCwsRole(userToken(OFFICE_ADMIN, CALS_ADMIN)),
        is(Optional.of(OFFICE_ADMIN)));
  }

  @Test
  public void testIsCalsAdmin() {
    assertTrue(UserRolesService.isCalsAdmin(userToken(CALS_ADMIN)));
    assertTrue(UserRolesService.isCalsAdmin(userToken(CALS_ADMIN, OFFICE_ADMIN)));
    assertTrue(UserRolesService.isCalsAdmin(userToken(STATE_ADMIN, CALS_ADMIN)));
    assertFalse(UserRolesService.isCalsAdmin(userToken(IDM_JOB)));
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
    assertFalse(UserRolesService.isCountyAdmin(userToken(IDM_JOB)));
    assertFalse(UserRolesService.isCountyAdmin(userToken(OFFICE_ADMIN, CALS_ADMIN)));
    assertFalse(UserRolesService.isCountyAdmin(userToken()));
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
