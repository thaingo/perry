package gov.ca.cwds.web;

import gov.ca.cwds.service.WhiteList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dmitry.rudenko on 10/3/2017.
 */
@Ignore
public class PerryLogoutSuccessHandlerTest {
  public static final String CALLBACK = "callback";
  private HttpServletResponse response;
  private HttpServletRequest request;
  private PerryLogoutSuccessHandler perryLogoutSuccessHandler;


  @Before
  public void before() {
    perryLogoutSuccessHandler = new PerryLogoutSuccessHandler();
    perryLogoutSuccessHandler.whiteList = Mockito.mock(WhiteList.class);
    response = Mockito.mock(HttpServletResponse.class);
    request = Mockito.mock(HttpServletRequest.class);
  }

  @Test
  public void test() throws IOException, ServletException {
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(request.getParameter(CALLBACK)).thenReturn(CALLBACK);
    perryLogoutSuccessHandler.onLogoutSuccess(request, response, authentication);
    Mockito.verify(response).sendRedirect(CALLBACK);
  }
}
