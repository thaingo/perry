package gov.ca.cwds.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class TraceTrackDenialFilter extends GenericFilterBean {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      final HttpServletRequest httpRequest = (HttpServletRequest) request;
      final HttpServletResponse httpResponse = (HttpServletResponse) response;
      final String method = httpRequest.getMethod();
      if ("TRACE".equalsIgnoreCase(method) || "TRACK".equalsIgnoreCase(method)) {
        httpResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return;
      }
    }

    chain.doFilter(request, response);
  }

}
