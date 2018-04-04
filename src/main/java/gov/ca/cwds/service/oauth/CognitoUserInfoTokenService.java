package gov.ca.cwds.service.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import gov.ca.cwds.service.http.CognitoHeaders;

@Profile("cognito")
@ConfigurationProperties(prefix = "cognito")
public class CognitoUserInfoTokenService extends CaresUserInfoTokenService {


  private String getUserTarget;
  private HttpHeaders headers;

  @Autowired
  private CognitoHeaders cognitoHeaders;

  @Autowired
  public CognitoUserInfoTokenService(ResourceServerProperties resourceServerProperties) {
    super(resourceServerProperties);
  }

  private HttpHeaders headers() {
    if (headers == null) {
      headers = cognitoHeaders.getHeadersForApiCall(getUserTarget);
    }
    return headers;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected HttpEntity httpEntityForGetUser(String accessToken) {
    String json = String.format("{\"AccessToken\": \"%s\"}", accessToken);
    return new HttpEntity<String>(json, headers());
  }

  public String getGetUserTarget() {
    return getUserTarget;
  }

  public void setGetUserTarget(String getUserTarget) {
    this.getUserTarget = getUserTarget;
  }

}
