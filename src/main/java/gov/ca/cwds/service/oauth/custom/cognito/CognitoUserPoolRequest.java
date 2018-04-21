package gov.ca.cwds.service.oauth.custom.cognito;


import org.codehaus.jackson.annotate.JsonProperty;

public class CognitoUserPoolRequest {

  private String accessToken;

  @JsonProperty("AccessToken")
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
