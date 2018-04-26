package gov.ca.cwds.web;

import java.io.IOException;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.service.WhiteList;

/**
 * @author CWDS CALS API Team
 */
@Component
public class PerryLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler
    implements LogoutSuccessHandler {

  private WhiteList whiteList;

  private PerryProperties properties;

  private LogoutUrlProvider logoutUrlProvider;

  @PostConstruct
  public void init() {
    if (StringUtils.isNotBlank(properties.getHomePageUrl())) {
      this.setDefaultTargetUrl(properties.getHomePageUrl());
      this.setAlwaysUseDefaultTargetUrl(true);
    }
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    String callback = request.getParameter("callback");
    if (!tryRedirect(response, callback)) {
      super.onLogoutSuccess(request, response, authentication);
    }
  }

  @SuppressFBWarnings("UNVALIDATED_REDIRECT") // white list usage right before redirect
  private boolean tryRedirect(HttpServletResponse response, String callback) throws IOException {
    Optional.ofNullable(callback).ifPresent(c -> whiteList.validate("callback", c));
    Optional<String> redirectUrl = logoutUrlProvider.apply(callback);
    if(redirectUrl.isPresent()) {
      response.sendRedirect(redirectUrl.get());
      return true;
    }
    return false;
  }

  @Autowired
  public void setLogoutUrlProvider(LogoutUrlProvider logoutUrlProvider) {
    this.logoutUrlProvider = logoutUrlProvider;
  }

  @Autowired
  public void setProperties(PerryProperties properties) {
    this.properties = properties;
  }

  @Autowired
  public void setWhiteList(WhiteList whiteList) {
    this.whiteList = whiteList;
  }
}
