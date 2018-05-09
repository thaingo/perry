package gov.ca.cwds.service.scripts;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Map;
import gov.ca.cwds.UniversalUserToken;
import org.apache.commons.compress.utils.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class UserInfoHotFixTest {
  private static final String IDP_SCRIPT = "/scripts/saf/idp.groovy";

  @Test
  public void testNoRoles() throws Exception {
    UniversalUserToken universalUserToken = map(IDP_SCRIPT, "/scripts/saf/saf-user-info-no-roles.json");
    Assert.assertTrue(universalUserToken.getRoles().isEmpty());
  }

  @Test
  public void testRoles() throws Exception {
    UniversalUserToken universalUserToken = map(IDP_SCRIPT, "/scripts/saf/saf-user-info-roles.json");
    Assert.assertEquals(1, universalUserToken.getRoles().size());
    Assert.assertTrue(universalUserToken.getRoles().contains("External CALS"));
  }

  @Test
  public void testWrongRoles() throws Exception {
    UniversalUserToken universalUserToken = map(IDP_SCRIPT, "/scripts/saf/saf-user-info-wrong-roles.json");
    Assert.assertTrue(universalUserToken.getRoles().isEmpty());
  }

  private UniversalUserToken map(String script, String json) throws Exception {
    String path = Paths.get(getClass().getResource(script).toURI()).toString();
    IdpMappingScript idpMappingScript = new IdpMappingScript(path);
    String userInfoJson = new String(
        IOUtils.toByteArray(getClass().getResourceAsStream(json)),
        Charset.defaultCharset());
    ObjectMapper objectMapper = new ObjectMapper();
    Map userInfoMap = objectMapper.readValue(userInfoJson, Map.class);
   return idpMappingScript.map(userInfoMap);
  }
}
