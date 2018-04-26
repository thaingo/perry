package gov.ca.cwds.service.sso.custom.cognito;

import java.io.IOException;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.service.sso.custom.OAuth2RequestCustomizer;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;


import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Service
@Profile("cognito")
public class CognitoUserInfoCustomizer extends OAuth2RequestCustomizer {
  private ObjectMapper objectMapper;

  @Autowired
  public CognitoUserInfoCustomizer(@Value("${security.oauth2.resource.userInfoUri}") String url) {
    super(url);
    objectMapper = new ObjectMapper();
  }


  @Override
  public HttpEntity apply(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CONTENT_TYPE, "application/x-amz-json-1.1");
    headers.set("X-Amz-Target", "AWSCognitoIdentityProviderService.GetUser");
    CognitoUserPoolRequest request = new CognitoUserPoolRequest();
    request.setAccessToken(accessToken);
    try {
      String userPoolRequest = objectMapper.writeValueAsString(request);
      return new HttpEntity<>(userPoolRequest, headers);
    } catch (IOException e) {
      throw new PerryException("Failed to prepare Cognito User Pool request", e);
    }
  }
}
