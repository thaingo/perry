package gov.ca.cwds.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@Profile("prod")
@Primary
@ConfigurationProperties(prefix = "cognito")
public class CognitoLoginService extends LoginServiceImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(CognitoLoginService.class);

  private String revokeTokenTarget;
  private String authorization;

  @Value("${security.oauth2.client.clientId}")
  private String clientId;

  @Autowired
  private CognitoHeaders cognitoHeaders;


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
      LOGGER.debug("validationHeader:" + validationHeaders.toString());
    }
    return validationHeaders;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected HttpEntity httpEntityForValidation(OAuth2AccessToken accessToken) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "refresh_token");
    params.add("client_id", clientId);
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
}
