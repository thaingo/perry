package gov.ca.cwds.service.mfa.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CognitoResponse {

  @JsonProperty("refreshToken")
  private RefreshToken refreshToken;
  @JsonProperty("accessToken")
  private AccessToken accessToken;

  @JsonProperty("refreshToken")
  public RefreshToken getRefreshToken() {
    return refreshToken;
  }

  @JsonProperty("refreshToken")
  public void setRefreshToken(RefreshToken refreshToken) {
    this.refreshToken = refreshToken;
  }

  @JsonProperty("accessToken")
  public AccessToken getAccessToken() {
    return accessToken;
  }

  @JsonProperty("accessToken")
  public void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
  }

}
