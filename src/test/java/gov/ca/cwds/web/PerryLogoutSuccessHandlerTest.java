package gov.ca.cwds.web;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.service.WhiteList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by dmitry.rudenko on 10/3/2017.
 */

public class PerryLogoutSuccessHandlerTest {
  private static final String CALLBACK = "callback";
  private static final String LOGOUT_URL = "http://logoutUrl";
  private static final String HOME_PAGE_URL = "http://setHomePageUrl";
  private HttpServletResponse response;
  private HttpServletRequest request;
  private PerryLogoutSuccessHandler perryLogoutSuccessHandler;

  @Before
  public void before() {
    perryLogoutSuccessHandler = new PerryLogoutSuccessHandler();
    perryLogoutSuccessHandler.setWhiteList(Mockito.mock(WhiteList.class));
    response = Mockito.mock(HttpServletResponse.class);
    Mockito.when(response.encodeRedirectURL(HOME_PAGE_URL)).thenReturn(HOME_PAGE_URL);
    Mockito.when(response.encodeRedirectURL(LOGOUT_URL)).thenReturn(LOGOUT_URL);
    request = Mockito.mock(HttpServletRequest.class);
    PerryProperties properties = new PerryProperties();
    properties.setHomePageUrl(HOME_PAGE_URL);
    perryLogoutSuccessHandler.setProperties(properties);
    perryLogoutSuccessHandler.init();
  }

  @Test
  public void testValidUrlProvided() throws IOException, ServletException {
    LogoutUrlProvider logoutUrlProvider = Mockito.mock(LogoutUrlProvider.class);
    Mockito.when(logoutUrlProvider.apply(CALLBACK)).thenReturn(Optional.of(LOGOUT_URL));
    perryLogoutSuccessHandler.setLogoutUrlProvider(logoutUrlProvider);
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(request.getParameter(CALLBACK)).thenReturn(CALLBACK);
    perryLogoutSuccessHandler.onLogoutSuccess(request, response, authentication);
    Mockito.verify(response).sendRedirect(LOGOUT_URL);
  }

  @Test
  public void testInit() throws IOException, ServletException {
    LogoutUrlProvider logoutUrlProvider = Mockito.mock(LogoutUrlProvider.class);
    Mockito.when(logoutUrlProvider.apply(CALLBACK)).thenReturn(Optional.empty());
    perryLogoutSuccessHandler.setLogoutUrlProvider(logoutUrlProvider);
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(request.getParameter(CALLBACK)).thenReturn(CALLBACK);
    perryLogoutSuccessHandler.onLogoutSuccess(request, response, authentication);
    Mockito.verify(response).sendRedirect(HOME_PAGE_URL);
  }
}
