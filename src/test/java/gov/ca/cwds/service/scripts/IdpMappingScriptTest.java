package gov.ca.cwds.service.scripts;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by dmitry.rudenko on 7/28/2017.
 */
public class IdpMappingScriptTest {
  @Test
  public void testGroovyMapping() throws Exception {
    String path = Paths.get(getClass().getResource("/idp.groovy").toURI()).toString();
    IdpMappingScript idpMappingScript = new IdpMappingScript(path);
    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("safid.racfid", "racfid");
    userInfo.put("ok", "true");

    NsUser nsUser = new NsUser();
    UniversalUserToken userToken = idpMappingScript.map(userInfo, nsUser);
    assertEquals("racfid", userToken.getUserId());
  }
}
