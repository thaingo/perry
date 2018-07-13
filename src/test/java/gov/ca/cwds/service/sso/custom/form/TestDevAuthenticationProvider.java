package gov.ca.cwds.service.sso.custom.form;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.PerryProperties.IdentityProviderConfiguration;
import gov.ca.cwds.PerryProperties.ServiceProviderConfiguration;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.config.Constants;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * Created by dmitry.rudenko on 10/2/2017.
 */
public class TestDevAuthenticationProvider {

  private DevAuthenticationProvider authenticationProvider;

  @Before
  public void before() throws Exception {
    authenticationProvider = new DevAuthenticationProvider();
    authenticationProvider.perryProperties = new PerryProperties();
    Map<String, ServiceProviderConfiguration> sps = new HashMap<>();
    ServiceProviderConfiguration sp = createSPConfig("mfa", "/mfa-mock/default.groovy");
    sps.put(sp.getId(), sp);
    authenticationProvider.perryProperties.setServiceProviders(sps);
    authenticationProvider.perryProperties
        .setIdentityProvider(createIDPConfig("/mfa-mock/cognito.groovy"));

  }

  private ServiceProviderConfiguration createSPConfig(String name, String resourcePath)
      throws Exception {
    ServiceProviderConfiguration sp = new ServiceProviderConfiguration();
    sp.setId(name);
    sp.setIdentityMapping(getAbsolutePath(resourcePath));
    return sp;
  }

  private IdentityProviderConfiguration createIDPConfig(String path) throws Exception {
    IdentityProviderConfiguration idp = new IdentityProviderConfiguration();
    idp.setIdpMapping(getAbsolutePath(path));
    return idp;
  }

  private String getAbsolutePath(String path) throws URISyntaxException {
    return new File(getClass().getResource(path).toURI()).getAbsolutePath();
  }

  private Authentication authentication() throws Exception {
    String cognitoResponse = IOUtils.toString(getClass().getResourceAsStream(
        "/mfa-mock/cognito-response.json"));
    return new UsernamePasswordAuthenticationToken(cognitoResponse, "password");
  }

  @Test
  public void testAuthNoFile() throws Exception {
    Authentication authentication = authenticationProvider.authenticate(authentication());
    JSONAssert.assertEquals(
        ((UniversalUserToken) authentication.getPrincipal())
            .getParameter(Constants.IDENTITY_JSON).toString(),
        IOUtils.toString(getClass().getResourceAsStream("/mfa-mock/token.json")),
        false);
  }

}
