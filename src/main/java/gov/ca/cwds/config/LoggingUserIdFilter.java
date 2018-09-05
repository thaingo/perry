package gov.ca.cwds.config;

import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class LoggingUserIdFilter extends GenericFilterBean {

  public static final String USER_ID = "USER_ID";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    try {
      Optional<String> userId = CurrentAuthenticatedUserUtil.getUserId();
      MDC.put(USER_ID, userId.orElse(null));

      chain.doFilter(request, response);
    } finally {
      MDC.remove(USER_ID);
    }
  }
}
