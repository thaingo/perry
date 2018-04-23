package gov.ca.cwds.web.custom.saf;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import gov.ca.cwds.web.LogoutUrlProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
