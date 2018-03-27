package gov.ca.cwds.config;


import gov.ca.cwds.service.WhiteList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static gov.ca.cwds.config.Constants.*;
import static javax.servlet.RequestDispatcher.ERROR_MESSAGE;

/**
 * Created by dmitry.rudenko on 9/14/2017.
 */
@Component
public class LoginServiceValidatorFilter extends GenericFilterBean {
  private WhiteList whiteList;

  private RequestMatcher requestMatcher = new AntPathRequestMatcher(LOGIN_SERVICE_URL);

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    if (requestMatcher.matches(httpServletRequest)) {
      try {
        validate(httpServletRequest);
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        servletRequest.setAttribute(ERROR_MESSAGE, e.getMessage());
        servletRequest.getRequestDispatcher("/" + ERROR_CONTROLLER).forward(servletRequest, servletResponse);
        return;
      }
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }

  protected void validate(HttpServletRequest servletRequest) {
    String callback = servletRequest.getParameter(CALLBACK_PARAM);
    if(StringUtils.isNotBlank(callback)) {
      whiteList.validate(CALLBACK_PARAM, callback);
    }
  }

  @Autowired
  public void setWhiteList(WhiteList whiteList) {
    this.whiteList = whiteList;
  }

  public void setRequestMatcher(RequestMatcher requestMatcher) {
    this.requestMatcher = requestMatcher;
  }
}
