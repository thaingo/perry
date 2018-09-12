package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.AuthorizeService.COUNTY_NAME;
import static gov.ca.cwds.idm.service.AuthorizeService.areNotNullAndEquals;
import static gov.ca.cwds.idm.service.AuthorizeService.findUser;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import java.util.Set;
import org.junit.Test;

public class AuthorizeServiceTest {

  @Test
  public void testAreNotNullAndEquals() {
    assertTrue(areNotNullAndEquals("abc", "abc"));
    assertFalse(areNotNullAndEquals("abc", "prst"));
    assertFalse(areNotNullAndEquals("abc", null));
    assertFalse(areNotNullAndEquals(null, "abc"));
    assertFalse(areNotNullAndEquals(null, null));
  }

  @Test
  public void findUserStateAdminSameCounty() {
    User user = user("Yolo");
    assertTrue(findUser(user, admin(toSet(STATE_ADMIN), "Yolo")));
  }

  @Test
  public void findUserStateAdminDifferentCounty() {
    User user = user("Madera");
    assertTrue(findUser(user, admin(toSet(STATE_ADMIN), "Yolo")));
  }

  @Test
  public void findUserCountyAdminSameCounty() {
    User user = user("Yolo");
    UniversalUserToken admin = admin(toSet(COUNTY_ADMIN),"Yolo");
    assertTrue(findUser(user, admin));
  }

  @Test
  public void findUserCountyAdminDifferentCounty() {
    User user = user("Yolo");
    UniversalUserToken admin = admin(toSet(COUNTY_ADMIN),"Madera");
    assertFalse(findUser(user, admin));
  }

  private User user(String countyName) {
    User user = new User();
    user.setCountyName(countyName);
    return user;
  }

  private UniversalUserToken admin(Set<String> roles, String countyName) {
    UniversalUserToken admin = new UniversalUserToken();
    admin.setRoles(roles);
    admin.setParameter(COUNTY_NAME, countyName);
    return admin;
  }
}
