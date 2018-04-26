package gov.ca.cwds.service.sso.custom.cognito;

import gov.ca.cwds.service.sso.custom.OAuth2RequestCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("cognito")
public class CognitoInvalidateCustomizer extends OAuth2RequestCustomizer {
  @Autowired
  public CognitoInvalidateCustomizer(@Value("${security.oauth2.resource.revokeTokenUri}") String url) {
    super(url);
  }

  @Override
  public HttpEntity apply(String s) {
    throw new UnsupportedOperationException("Unsupported: " + url);
  }
}
