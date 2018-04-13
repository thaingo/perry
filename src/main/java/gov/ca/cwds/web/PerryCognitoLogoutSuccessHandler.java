package gov.ca.cwds.web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Profile("cognito")
@Primary
@Component
@ConfigurationProperties(prefix = "security.oauth2.resource")
public class PerryCognitoLogoutSuccessHandler extends PerryLogoutSuccessHandler
    implements LogoutSuccessHandler {

  private URI logoutTokenUri;

  @Override
  @SuppressFBWarnings("UNVALIDATED_REDIRECT") // white list usage right before redirect
  protected boolean tryRedirect(HttpServletResponse response, String callback) throws IOException {
    response.sendRedirect(logoutTokenUri.toString());
    return true;
  }

  public void setLogoutTokenUri(String logoutTokenUri) throws URISyntaxException {
    this.logoutTokenUri = new URI(logoutTokenUri);
  }
}
