package gov.ca.cwds.config;

import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class LoggingFilter extends GenericFilterBean {

  public static final String REQUEST_ID = "REQUEST_ID";
  public static final String USER_ID = "USER_ID";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    try {
      final String uniqueId = UUID.randomUUID().toString();
      MDC.put(REQUEST_ID, uniqueId);

      final String userId = CurrentAuthenticatedUserUtil.getUserId();
      MDC.put(USER_ID, userId);

      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
