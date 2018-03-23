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
    if (request.getAuthorizationCode() != null && request.getPreservedState() == null) {
      AuthorizationCodeResourceDetails authorizationCodeResourceDetails = (AuthorizationCodeResourceDetails) details;
      String redirectUrl = authorizationCodeResourceDetails.getRedirectUri(request);
      String redirectUrlNoParams = getUrlWithoutParameters(redirectUrl);
      request.setPreservedState(redirectUrlNoParams);
    }
    return super.obtainAccessToken(details, request);
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