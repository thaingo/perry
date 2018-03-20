package gov.ca.cwds.security.test;

import gov.ca.cwds.security.PerryShiroToken;
import java.util.Optional;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

/**
 * @author CWDS CALS API Team
 */

public class TestSecurityFilter extends AuthenticatingFilter {

  public static final String PATH_TO_PRINCIPAL_FIXTURE = "pathToPrincipalFixture";
  private static final String PATH_TO_DEFAULT_PRINCIPAL_FIXTURE = "default-principal.json";

  @Override
  protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
    return new PerryShiroToken(Optional.ofNullable(request.getParameter(PATH_TO_PRINCIPAL_FIXTURE))
        .orElse(PATH_TO_DEFAULT_PRINCIPAL_FIXTURE));
  }

  @Override
  protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
      throws Exception {
    return executeLogin(request, response);
  }

}
