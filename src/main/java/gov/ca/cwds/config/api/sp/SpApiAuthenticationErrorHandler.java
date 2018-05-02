package gov.ca.cwds.config.api.sp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class SpApiAuthenticationErrorHandler implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
