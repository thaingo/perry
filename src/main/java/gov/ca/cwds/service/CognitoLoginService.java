package gov.ca.cwds.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import gov.ca.cwds.service.http.CognitoHeaders;

@Service
@Profile("cognito")
@Primary
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "cognito")
public class CognitoLoginService extends LoginServiceImpl {

  private String revokeTokenTarget;
  private String authorization;

  @Autowired
  private CognitoHeaders cognitoHeaders;

  Map<String, String> validateTokenBody;


  private HttpHeaders invalidationHeaders;
  private HttpHeaders validationHeaders;

  private HttpHeaders invalidationHeaders() {
    if (invalidationHeaders == null) {
      invalidationHeaders = cognitoHeaders.getHeadersForApiCall(revokeTokenTarget);
    }
    return invalidationHeaders;
  }

  private HttpHeaders validationHeaders() {
    if (validationHeaders == null) {
      validationHeaders = cognitoHeaders.getHeadersForApplicationFormUrlEncoded();
    }
    return validationHeaders;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected HttpEntity httpEntityForValidation(OAuth2AccessToken accessToken) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.setAll(validateTokenBody);
    params.add("refresh_token", accessToken.getRefreshToken().getValue());
    return new HttpEntity<>(params, validationHeaders());
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected HttpEntity httpEntityForInvalidation(OAuth2AccessToken accessToken) {
    String json = String.format("{\"AccessToken\": \"%s\"}", accessToken);
    return new HttpEntity<String>(json, invalidationHeaders());
  }

  public String getRevokeTokenTarget() {
    return revokeTokenTarget;
  }

  public void setRevokeTokenTarget(String revokeTokenTarget) {
    this.revokeTokenTarget = revokeTokenTarget;
  }

  public String getAuthorization() {
    return authorization;
  }

  public void setAuthorization(String authorization) {
    this.authorization = authorization;
  }

  public Map<String, String> getValidateTokenBody() {
    return validateTokenBody;
  }

  public void setValidateTokenBody(Map<String, String> validateTokenBody) {
    this.validateTokenBody = validateTokenBody;
  }
}
