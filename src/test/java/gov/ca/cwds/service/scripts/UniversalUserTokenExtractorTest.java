package gov.ca.cwds.service.scripts;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.NsUserService;
import gov.ca.cwds.service.sso.UniversalUserTokenExtractor;
import java.util.Optional;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    NsUserService nsUserService = mock(NsUserService.class);
    when(nsUserService.findByUsername(any())).thenReturn(Optional.of(new NsUser()));
    userTokenExtractor.setNsUserService(nsUserService);

    userTokenExtractor.setConfiguration(perryProperties);
    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("safid.racfid", "racfid");
    userInfo.put("ok", "true");
    UniversalUserToken token = userTokenExtractor.extractPrincipal(userInfo);
    assertEquals("racfid", token.getUserId());
  }
}
