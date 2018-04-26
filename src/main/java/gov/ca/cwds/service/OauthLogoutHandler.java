package gov.ca.cwds.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import gov.ca.cwds.UniversalUserToken;

/**
 * @author CWDS CALS API Team
 */
@Profile({"prod"})
@Component
public class OauthLogoutHandler implements LogoutHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OauthLogoutHandler.class);
  private LoginService loginService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    try {
      if (authentication == null) {
        LOGGER.warn("Authentication is NULL");
        return;
      }
      loginService.invalidate(((UniversalUserToken) authentication.getPrincipal()).getToken());
    } catch (UnsupportedOperationException e) {
      LOGGER.debug("Token invalidation error.", e);
    } catch (Exception e) {
      LOGGER.error("Token invalidation error.", e);
    }
  }

  @Autowired
  public void setLoginService(LoginService loginService) {
    this.loginService = loginService;
  }
}
