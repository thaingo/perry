package gov.ca.cwds.web.custom.cognito;

import java.util.Optional;
import javax.annotation.PostConstruct;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.web.LogoutUrlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("cognito")
@Component
public class CognitoLogoutUrlProvider implements LogoutUrlProvider {
//https://perrysandbox.auth.us-west-2.amazoncognito.com/logout?response_type=code&client_id=5un7gv7i23kf0pn6pdlas8vjc4&redirect_uri=http://localhost:8080/perry/authn/login?callback=http://google.com
  private static final String LOGOUT_URL_TEMPLATE = "%s?response_type=code&client_id=%s&redirect_uri=%s";

  @Value("${security.oauth2.resource.logoutTokenUri}")
  private String logoutTokenUri;
  @Autowired
  private ResourceServerProperties resourceServerProperties;
  private String logoutUrl;
  @Autowired
  private PerryProperties properties;

  @PostConstruct
  public void init() {
    if(properties.getAuthorizationCodeEndpoint() == null) {
      throw new PerryException("Authorization code endpoint is not set. Example: https://domain.com/perry/login");
    }
    logoutUrl = String.format(LOGOUT_URL_TEMPLATE,
        logoutTokenUri,
        resourceServerProperties.getClientId(),
        properties.getAuthorizationCodeEndpoint());
  }

  @Override
  public Optional<String> apply(String callback) {
    return Optional.of(logoutUrl);
  }
}
