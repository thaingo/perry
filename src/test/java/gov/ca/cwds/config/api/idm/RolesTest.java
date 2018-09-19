package gov.ca.cwds.config.api.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.IDM_JOB;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.getAdminRoles;
import static gov.ca.cwds.config.api.idm.Roles.getStrongestAdminRole;
import static gov.ca.cwds.config.api.idm.Roles.isAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isCalsAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyCountyAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyOfficeAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyStateAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isNonRacfIdCalsUser;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.UniversalUserToken;
import java.util.Set;
import org.junit.Test;

public class RolesTest {

  @Test
  public void testGetAdminRoles() {
    Set<String> adminRoles = getAdminRoles();
    assertThat(adminRoles, hasSize(3));
    assertTrue(adminRoles.contains(COUNTY_ADMIN));
    assertTrue(adminRoles.contains(STATE_ADMIN));
    assertTrue(adminRoles.contains(OFFICE_ADMIN));
  }

  @Test
  public void testIsAdmin() {
    assertFalse(isAdmin(userToken()));
    assertFalse(isAdmin(userToken(CWS_WORKER)));
    assertFalse(isAdmin(userToken(IDM_JOB)));
    assertFalse(isAdmin(userToken(CALS_EXTERNAL_WORKER, CWS_WORKER)));
    assertTrue(isAdmin(userToken(CWS_WORKER, COUNTY_ADMIN)));
    assertTrue(isAdmin(userToken(STATE_ADMIN, OFFICE_ADMIN)));
    assertTrue(isAdmin(userToken(OFFICE_ADMIN)));
  }

  @Test
  public void testIsNonRacfIdCalsUser() {
    assertFalse(isNonRacfIdCalsUser(userToken()));
    assertFalse(isNonRacfIdCalsUser(userToken(CWS_WORKER)));
    assertFalse(isNonRacfIdCalsUser(userToken(COUNTY_ADMIN)));
    assertTrue(isNonRacfIdCalsUser(userToken(CALS_EXTERNAL_WORKER)));
    assertTrue(isNonRacfIdCalsUser(userToken(OFFICE_ADMIN, CALS_EXTERNAL_WORKER)));
    assertTrue(isNonRacfIdCalsUser(userToken(CALS_EXTERNAL_WORKER, CWS_WORKER)));
  }

  @Test
  public void testGetStrongestAdminRole() {
    assertNull(getStrongestAdminRole(userToken()));
    assertNull(getStrongestAdminRole(userToken(IDM_JOB)));

    assertThat(getStrongestAdminRole(userToken(STATE_ADMIN)), is(STATE_ADMIN));
    assertThat(getStrongestAdminRole(userToken(COUNTY_ADMIN)), is(COUNTY_ADMIN));
    assertThat(getStrongestAdminRole(userToken(OFFICE_ADMIN)), is(OFFICE_ADMIN));
    assertThat(getStrongestAdminRole(userToken(STATE_ADMIN, COUNTY_ADMIN)), is(STATE_ADMIN));
    assertThat(getStrongestAdminRole(userToken(OFFICE_ADMIN, COUNTY_ADMIN)), is(COUNTY_ADMIN));
    assertThat(getStrongestAdminRole(userToken(OFFICE_ADMIN, STATE_ADMIN)), is(STATE_ADMIN));
  }

  @Test
  public void testIsMostlyStateAdmin() {
    assertTrue(isMostlyStateAdmin(userToken(OFFICE_ADMIN, STATE_ADMIN, COUNTY_ADMIN)));
    assertTrue(isMostlyStateAdmin(userToken(STATE_ADMIN, COUNTY_ADMIN)));
    assertTrue(isMostlyStateAdmin(userToken(STATE_ADMIN)));
    assertFalse(isMostlyStateAdmin(userToken(COUNTY_ADMIN, OFFICE_ADMIN)));
    assertFalse(isMostlyStateAdmin(userToken(IDM_JOB)));
    assertFalse(isMostlyStateAdmin(userToken(CALS_ADMIN)));
    assertFalse(isMostlyStateAdmin(userToken()));
  }

  @Test
  public void testIsMostlyCountyAdmin() {
    assertTrue(isMostlyCountyAdmin(userToken(OFFICE_ADMIN, COUNTY_ADMIN)));
    assertTrue(isMostlyCountyAdmin(userToken(COUNTY_ADMIN)));
    assertFalse(isMostlyCountyAdmin(userToken(OFFICE_ADMIN)));
    assertFalse(isMostlyCountyAdmin(userToken(IDM_JOB)));
    assertFalse(isMostlyCountyAdmin(userToken(CALS_ADMIN)));
    assertFalse(isMostlyCountyAdmin(userToken()));
  }

  @Test
  public void testIsMostlyOfficeAdmin() {
    assertTrue(isMostlyOfficeAdmin(userToken(OFFICE_ADMIN)));
    assertFalse(isMostlyOfficeAdmin(userToken(IDM_JOB)));
    assertFalse(isMostlyOfficeAdmin(userToken(CALS_ADMIN)));
    assertFalse(isMostlyOfficeAdmin(userToken()));
  }

  @Test
  public void testIsCalsAdmin() {
    assertTrue(isCalsAdmin(userToken(CALS_ADMIN)));
    assertTrue(isCalsAdmin(userToken(CALS_ADMIN, OFFICE_ADMIN)));
    assertTrue(isCalsAdmin(userToken(STATE_ADMIN, CALS_ADMIN)));
    assertFalse(isCalsAdmin(userToken(IDM_JOB)));
    assertFalse(isCalsAdmin(userToken(OFFICE_ADMIN, COUNTY_ADMIN)));
    assertFalse(isCalsAdmin(userToken()));
  }

  static private UniversalUserToken userToken(String... roles) {
    UniversalUserToken token = new UniversalUserToken();
    token.setRoles(toSet(roles));
    return token;
  }
}
