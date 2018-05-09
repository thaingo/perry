package gov.ca.cwds.config.api.common;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class SpApiSecurityFilter extends AbstractPreAuthenticatedProcessingFilter {
  private static final String TOKEN_PARAMETER_NAME = "token";

  public SpApiSecurityFilter(AuthenticationManager authenticationManager) {
    this.setAuthenticationManager(authenticationManager);
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    return request.getParameter(TOKEN_PARAMETER_NAME);
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

}
