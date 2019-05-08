package gov.ca.cwds.config.api.common;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class SpApiAccessDeniedHandler implements AccessDeniedHandler {
  @Override
  @SuppressWarnings({"findsecbugs:XSS_SERVLET"})//Printed constant string cannot contain XSS script
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    response.getOutputStream().print(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
  }
}
