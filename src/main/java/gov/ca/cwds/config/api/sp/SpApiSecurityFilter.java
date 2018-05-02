package gov.ca.cwds.config.api.sp;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import gov.ca.cwds.service.TokenService;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SpApiSecurityFilter extends AbstractPreAuthenticatedProcessingFilter implements AuthenticationManager {
  private static final String TOKEN_PARAMETER_NAME = "token";
  @Autowired
  private TokenService tokenService;

  public SpApiSecurityFilter() {
    setAuthenticationManager(this);
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    return request.getParameter(TOKEN_PARAMETER_NAME);
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    PerryTokenEntity perryTokenEntity = tokenService.getPerryToken((String) authentication.getPrincipal());
    if (perryTokenEntity == null) {
      fail();
    }

    return new PreAuthenticatedAuthenticationToken(
        perryTokenEntity.getUser(),
        SerializationUtils.deserialize(perryTokenEntity.getSecurityContext()),
        Collections.singletonList(new SimpleGrantedAuthority("SP_API_CLIENT")));
  }

  private void fail() {
    throw new BadCredentialsException("invalid token");
  }
}
