package gov.ca.cwds.config.api.idm;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.IDM_JOB;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.getAdminRoles;
import static gov.ca.cwds.config.api.idm.Roles.isAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isNonRacfIdCalsUser;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertFalse;
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

  static private UniversalUserToken userToken(String... roles) {
    UniversalUserToken token = new UniversalUserToken();
    token.setRoles(toSet(roles));
    return token;
  }
}
