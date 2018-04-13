package gov.ca.cwds.service.http;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cognito")
public class CognitoHeaders {

  private String apiVersion;
  private String authorization;

  public CognitoHeaders() {
    super();
  }

  public HttpHeaders getHeadersForApiCall(String target) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/" + apiVersion);
    headers.set("X-Amz-Target", target);

    return headers;
  }

  public HttpHeaders getHeadersForApplicationFormUrlEncoded() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set("Authorization", "Basic " + authorization);

    return headers;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public String getAuthorization() {
    return authorization;
  }

  public void setAuthorization(String authorization) {
    this.authorization = authorization;
  }
}
