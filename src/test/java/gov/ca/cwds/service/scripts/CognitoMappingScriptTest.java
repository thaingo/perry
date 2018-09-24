package gov.ca.cwds.service.scripts;

import java.nio.file.Paths;
import java.util.*;
import gov.ca.cwds.UniversalUserToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

/**
 * Created by dmitry.rudenko on 7/28/2017.
 */
public class CognitoMappingScriptTest {
  @Test
  public void testUserAndRoles() throws Exception {
    String path = Paths.get(getClass().getResource("/scripts/cognito/cognito.groovy").toURI()).toString();
    IdpMappingScript idpMappingScript = new IdpMappingScript(path);
    Map userInfo =  new ObjectMapper()
        .readValue(getClass().getResourceAsStream("/scripts/cognito/cognito.json"), Map.class);

    UniversalUserToken userToken = idpMappingScript.map(userInfo);
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
    Assert.assertEquals("17", userToken.getParameter("custom:office"));
  }

  @Test
  public void testUserNoRoles() throws Exception {
    String path = Paths.get(getClass().getResource("/scripts/cognito/cognito.groovy").toURI()).toString();
    IdpMappingScript idpMappingScript = new IdpMappingScript(path);
    Map userInfo =  new ObjectMapper()
        .readValue(getClass().getResourceAsStream("/scripts/cognito/cognito-no-roles.json"), Map.class);

    UniversalUserToken userToken = idpMappingScript.map(userInfo);
    Assert.assertEquals("RACFID", userToken.getUserId());
    Assert.assertTrue(userToken.getRoles().isEmpty());
    Assert.assertNull(userToken.getParameter("custom:office"));
  }
}
