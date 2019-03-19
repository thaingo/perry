package gov.ca.cwds.service.sso.custom.saf;

import java.util.Optional;
import gov.ca.cwds.web.LogoutUrlProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author CWDS CALS API Team
 */
@Profile("saf")
@Component
public class SAFLogoutUrlProvider implements LogoutUrlProvider {
  @Value("${security.oauth2.resource.logoutTokenUri}")
  private String logoutTokenUri;

  @Override
  public Optional<String> apply(String callback) {
    StringBuilder safLogoutUrlBuilder = new StringBuilder(logoutTokenUri);
    safLogoutUrlBuilder.append('?');
    Optional
        .ofNullable(callback)
        .ifPresent(s -> safLogoutUrlBuilder.append("redirectToClientLogin=").append(s).append("&message="));
    return Optional.of(safLogoutUrlBuilder.toString());
  }

}
