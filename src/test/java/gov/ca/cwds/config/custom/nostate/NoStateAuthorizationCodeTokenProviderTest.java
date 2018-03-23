package gov.ca.cwds.config.custom.nostate;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

public class NoStateAuthorizationCodeTokenProviderTest {
  @Test
  public void testIgnoreState() {
    NoStateAuthorizationCodeTokenProvider noStateAuthorizationCodeTokenProvider = new NoStateAuthorizationCodeTokenProvider();
    AuthorizationCodeResourceDetails details = Mockito.mock(AuthorizationCodeResourceDetails.class);
    AccessTokenRequest request = new DefaultAccessTokenRequest();
    Mockito.when(details.getRedirectUri(request)).thenReturn("http://test.com?test=param");
    request.setAuthorizationCode("code");
    request.setPreservedState(null);

    noStateAuthorizationCodeTokenProvider.ignoreState(details, request);
    assert request.getPreservedState().equals("http://test.com");
  }
}
