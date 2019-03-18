package gov.ca.cwds.service.mfa;

import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.service.mfa.model.AuthParameters;
import gov.ca.cwds.service.mfa.model.CognitoResponse;
import gov.ca.cwds.service.mfa.model.RefreshRequest;
import gov.ca.cwds.service.mfa.model.RefreshResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

@Service
@Profile("mfa")
public class CognitoResponseService {

  private static final String CONTEXT_ATTR_NAME = "OAUTH2_CLIENT_CONTEXT";
  private static final String DEVICE_KEY_ATTR_NAME = "DEVICE_KEY";
  private ObjectMapper objectMapper = new ObjectMapper();

  @SuppressWarnings("unchecked")
  public void put(OAuth2ClientContext clientContext, OAuth2Authentication authentication) {
    getDetails(authentication).put(CONTEXT_ATTR_NAME, clientContext);
  }

  public OAuth2ClientContext convert(String json) {
    try {
      CognitoResponse cognitoResponse = objectMapper.readValue(json, CognitoResponse.class);
      return convert(cognitoResponse);
    } catch (IOException e) {
      throw new PerryException("Error processing Cognito json: " + json, e);
    }
  }

  public OAuth2ClientContext get(OAuth2Authentication authentication) {
    return (OAuth2ClientContext) getDetails(authentication).get(CONTEXT_ATTR_NAME);
  }

  public RefreshRequest refreshRequest(OAuth2ClientContext oAuth2ClientContext, String clientId) {
    RefreshRequest refreshRequest = new RefreshRequest();
    refreshRequest.setClientId(clientId);
    AuthParameters authParameters = new AuthParameters();
    String deviceKey = (String) oAuth2ClientContext.getAccessToken()
        .getAdditionalInformation().get(DEVICE_KEY_ATTR_NAME);
    authParameters.setDeviceKey(deviceKey);
    authParameters
        .setRefreshToken(oAuth2ClientContext.getAccessToken().getRefreshToken().getValue());
    refreshRequest.setAuthParameters(authParameters);
    return refreshRequest;
  }

  public void updateContext(OAuth2ClientContext context, RefreshResponse response) {
    DefaultOAuth2AccessToken accessToken = (DefaultOAuth2AccessToken) context.getAccessToken();
    accessToken.setExpiration(expiresInToDate(response.getAuthenticationResult().getExpiresIn()));
    accessToken.setValue(response.getAuthenticationResult().getAccessToken());
  }

  private OAuth2ClientContext convert(CognitoResponse cognitoResponse) {
    OAuth2ClientContext result = new DefaultOAuth2ClientContext();
    String accessToken = cognitoResponse.getAccessToken().getJwtToken();
    Date exp = expToDate(cognitoResponse.getAccessToken().getPayload().getExp());
    String refreshToken = cognitoResponse.getRefreshToken().getToken();
    DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
    oAuth2AccessToken.setExpiration(exp);
    Map<String, Object>  additionalInfo = new HashMap<>();
    additionalInfo.put(
        DEVICE_KEY_ATTR_NAME, cognitoResponse.getAccessToken().getPayload().getDeviceKey()
    );
    oAuth2AccessToken.setAdditionalInformation(additionalInfo);
    OAuth2RefreshToken oAuth2RefreshToken = new DefaultOAuth2RefreshToken(refreshToken);
    oAuth2AccessToken.setRefreshToken(oAuth2RefreshToken);
    result.setAccessToken(new DefaultOAuth2AccessToken(oAuth2AccessToken));
    return result;
  }

  private Map getDetails(OAuth2Authentication authentication) {
    try {
      Optional<Map> details = Optional
          .ofNullable((Map) authentication.getUserAuthentication().getDetails());
      return details.orElseThrow(() -> new PerryException("There are no user details"));
    } catch (NullPointerException e) {
      throw new PerryException("OAuth2Authentication wasn't created properly", e);
    }
  }

  private static Date expToDate(int exp) {
    return new Date(exp * 1000L);
  }

  private static Date expiresInToDate(int expiresIn) {
    long expiration = System.currentTimeMillis() + expiresIn * 1000L;
    return new Date(expiration);
  }
}
