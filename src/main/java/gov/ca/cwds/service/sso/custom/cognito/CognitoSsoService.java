package gov.ca.cwds.service.sso.custom.cognito;

import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.service.mfa.CognitoResponseService;
import gov.ca.cwds.service.mfa.model.RefreshRequest;
import gov.ca.cwds.service.mfa.model.RefreshResponse;
import gov.ca.cwds.service.sso.OAuth2Service;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Primary
@Profile("cognito_refresh")
public class CognitoSsoService extends OAuth2Service {

  @Autowired
  private CognitoResponseService cognitoResponseService;
  private RestTemplate restTemplate = new RestTemplate();

  @Override
  protected Optional<PerryTokenEntity> validateIdp(
      PerryTokenEntity perryTokenEntity,
      OAuth2ClientContext oAuth2ClientContext) {
    if (oAuth2ClientContext.getAccessToken().isExpired()) {
      RefreshRequest request = cognitoResponseService.refreshRequest(
          oAuth2ClientContext,
          resourceServerProperties.getClientId()
      );
      RefreshResponse response = refresh(request);
      cognitoResponseService.updateContext(oAuth2ClientContext, response);
    }
    doPost(
        restTemplate,
        resourceServerProperties.getTokenInfoUri(),
        oAuth2ClientContext.getAccessToken().getValue()
    );
    return refresh(oAuth2ClientContext, perryTokenEntity);
  }

  private RefreshResponse refresh(RefreshRequest refreshRequest) {
    try {
      String stringRequest = objectMapper.writeValueAsString(refreshRequest);
      HttpEntity<String> request = new HttpEntity<>(stringRequest, refreshHeaders());
      String response = restTemplate.exchange(
          resourceServerProperties.getUserInfoUri(),
          HttpMethod.POST,
          request,
          String.class
      ).getBody();
      return objectMapper.readValue(response, RefreshResponse.class);
    } catch (IOException e) {
      throw new PerryException(e.getMessage(), e);
    }
  }

  private HttpHeaders refreshHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.put("x-amz-target",
        Collections.singletonList("AWSCognitoIdentityProviderService.InitiateAuth"));
    httpHeaders.put("content-type",
        Collections.singletonList("application/x-amz-json-1.1"));
    httpHeaders.put("x-amz-user-agent",
        Collections.singletonList("aws-amplify/0.1.x js"));
    return httpHeaders;
  }

}
