package gov.ca.cwds.web;

import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.junit.ExpectedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

public class PerryCognitoLogoutSuccessHandlerTest {

  private HttpServletResponse response;
  private HttpServletRequest request;
  private PerryCognitoLogoutSuccessHandler perryCognitoLogoutSuccessHandler;

  @Rule
  public ExpectedException thrown = ExpectedException.none();


  @Before
  public void before() {
    perryCognitoLogoutSuccessHandler = new PerryCognitoLogoutSuccessHandler();
    response = Mockito.mock(HttpServletResponse.class);
    request = Mockito.mock(HttpServletRequest.class);
  }

  @Test
  public void testSetThrowsExceptionOnBadUri() throws Exception {
    thrown.expect(URISyntaxException.class);

    perryCognitoLogoutSuccessHandler.setLogoutTokenUri("bad uri");
  }

  @Test
  public void testRedirectSuccessfully() throws Exception {
    String redirectString = "http://www.google.com";

    Authentication authentication = Mockito.mock(Authentication.class);

    perryCognitoLogoutSuccessHandler.setLogoutTokenUri(redirectString);
    perryCognitoLogoutSuccessHandler.onLogoutSuccess(request, response, authentication);
    Mockito.verify(response).sendRedirect(redirectString);
  }
}
