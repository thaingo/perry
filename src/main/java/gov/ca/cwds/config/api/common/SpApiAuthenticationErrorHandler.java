package gov.ca.cwds.config.api.common;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class SpApiAuthenticationErrorHandler implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    response.getOutputStream().print(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
  }
}
