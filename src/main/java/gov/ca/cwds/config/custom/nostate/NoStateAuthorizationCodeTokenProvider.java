package gov.ca.cwds.config.custom.nostate;

import gov.ca.cwds.rest.api.domain.PerryException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.net.URI;

/**
 * Created by TPT2 on 1/10/2018.
 */
public class NoStateAuthorizationCodeTokenProvider extends AuthorizationCodeAccessTokenProvider {

  @Override
  public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest request) {
    ignoreState((AuthorizationCodeResourceDetails) details, request);
    return super.obtainAccessToken(details, request);
  }

  void ignoreState(AuthorizationCodeResourceDetails details, AccessTokenRequest request) {
    if (request.getAuthorizationCode() != null && request.getPreservedState() == null) {
      String redirectUrl = details.getRedirectUri(request);
      String redirectUrlNoParams = getUrlWithoutParameters(redirectUrl);
      request.setPreservedState(redirectUrlNoParams);
    }
  }

  private String getUrlWithoutParameters(String url) {
    try {
      URI uri = new URI(url);
      return new URI(uri.getScheme(),
              uri.getAuthority(),
              uri.getPath(),
              null, // Ignore the query part of the input url
              uri.getFragment()).toString();
    } catch (Exception e) {
      throw new PerryException("Can't create redirect url", e);
    }
  }
}