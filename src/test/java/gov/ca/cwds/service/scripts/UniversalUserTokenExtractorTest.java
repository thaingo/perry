package gov.ca.cwds.service.scripts;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.service.sso.UniversalUserTokenExtractor;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by dmitry.rudenko on 7/31/2017.
 */
public class UniversalUserTokenExtractorTest {
  @Test
  public void test() throws Exception {
    PerryProperties perryProperties = new PerryProperties();
    PerryProperties.IdentityProviderConfiguration identityProviderConfiguration = new PerryProperties.IdentityProviderConfiguration();
    String path = Paths.get(getClass().getResource("/idp.groovy").toURI()).toString();
    identityProviderConfiguration.setIdpMapping(path);
    perryProperties.setIdentityProvider(identityProviderConfiguration);
    UniversalUserTokenExtractor userTokenExtractor = new UniversalUserTokenExtractor();
    userTokenExtractor.setConfiguration(perryProperties);
    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("safid.racfid", "racfid");
    userInfo.put("ok", "true");
    UniversalUserToken token = userTokenExtractor.extractPrincipal(userInfo);
    assertEquals("racfid", token.getUserId());
  }
}
