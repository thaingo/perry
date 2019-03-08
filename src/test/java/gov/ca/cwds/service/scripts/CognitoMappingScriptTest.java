package gov.ca.cwds.service.scripts;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by dmitry.rudenko on 7/28/2017.
 */
public class CognitoMappingScriptTest {

  private IdpMappingScript idpMappingScript;

  @Before
  public void before() throws Exception {
    String path = Paths.get(getClass().getResource("/scripts/cognito.groovy").toURI()).toString();
    idpMappingScript = new IdpMappingScript(path);
  }

  @Test
  public void testUserAndRoles() throws Exception {
    Map userInfo =  new ObjectMapper()
        .readValue(getClass().getResourceAsStream("/scripts/cognito/cognito.json"), Map.class);

    NsUser nsUser = new NsUser();

    UniversalUserToken userToken = idpMappingScript.map(userInfo, nsUser);
    Assert.assertEquals("RACFID", userToken.getUserId());
    Assert.assertEquals(new HashSet<>(Arrays.asList(
        "first-role",
        "second-role",
        "third-role")), userToken.getRoles());
    Assert.assertEquals(new HashSet<>(Arrays.asList(
        "first-permission",
        "second-permission",
        "third-permission")), userToken.getPermissions());
    Assert.assertTrue(userToken.getParameter("custom:permission") instanceof Set);
    Assert.assertEquals("perry", userToken.getParameter("userName"));
    Assert.assertEquals("17", userToken.getParameter("custom:office"));
  }

  @Test
  public void testUserNoRoles() throws Exception {
    Map userInfo =  new ObjectMapper()
        .readValue(getClass().getResourceAsStream("/scripts/cognito/cognito-no-roles.json"), Map.class);

    NsUser nsUser = new NsUser();

    UniversalUserToken userToken = idpMappingScript.map(userInfo, nsUser);
    Assert.assertEquals("RACFID", userToken.getUserId());
    Assert.assertTrue(userToken.getRoles().isEmpty());
    Assert.assertEquals("perry", userToken.getParameter("userName"));
    Assert.assertNull(userToken.getParameter("custom:office"));
  }
}
