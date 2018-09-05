package gov.ca.cwds.config;

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
public class LoggingRequestIdFilter extends GenericFilterBean {

  public static final String REQUEST_ID = "REQUEST_ID";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    try {
      final String uniqueId = UUID.randomUUID().toString();
      MDC.put(REQUEST_ID, uniqueId);

      chain.doFilter(request, response);
    } finally {
      MDC.remove(REQUEST_ID);
    }
  }
}
