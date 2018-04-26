package gov.ca.cwds.service.sso.custom.form;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.service.sso.custom.form.DevAuthenticationProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * Created by dmitry.rudenko on 10/2/2017.
 */
public class TestDevAuthenticationProvider {
  private DevAuthenticationProvider authenticationProvider;


  @Before
  public void before() {
    authenticationProvider = new DevAuthenticationProvider();
    authenticationProvider.perryProperties = new PerryProperties();
  }

  private Authentication authentication() {
    return new UsernamePasswordAuthenticationToken("{\"user\":\"user\"}", "password");
  }

  @Test
  public void testAuthNoFile() {
    Authentication authentication =
            authenticationProvider.authenticate(
                    authentication());
    assert authentication.getPrincipal() instanceof UniversalUserToken;
    assert ((UniversalUserToken)authentication.getPrincipal()).getUserId().equals("user");
    assert authentication.getCredentials().equals("N/A");
    assert authentication instanceof UsernamePasswordAuthenticationToken;
    assert authentication.getAuthorities().size() == 1;
    assert authentication.getAuthorities().iterator().next().getAuthority().equals("ROLE_USER");
  }

}
