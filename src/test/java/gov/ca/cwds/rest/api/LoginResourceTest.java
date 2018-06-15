package gov.ca.cwds.rest.api;

import org.junit.Test;

public class LoginResourceTest {
  @Test
  public void testAccessCodeWithParameters() throws Exception {
    LoginResource loginResource = new LoginResource();
    String callback = "http://host:8080/context/path?param1=value1";
    String urlWithAccessCode = loginResource.addAccessCode(callback, "123");
    assert urlWithAccessCode.equals("http://host:8080/context/path?param1=value1&accessCode=123");
  }

  @Test
  public void testAccessCodeWithoutParameters() throws Exception {
    LoginResource loginResource = new LoginResource();
    String callback = "http://host:8080/context/path";
    String urlWithAccessCode = loginResource.addAccessCode(callback, "123");
    assert urlWithAccessCode.equals("http://host:8080/context/path?accessCode=123");
  }

  @Test
  public void testAccessCodeWithParametersAndFragment() throws Exception {
    LoginResource loginResource = new LoginResource();
    String callback = "http://host:8080/context/path?param1=value1#fragment";
    String urlWithAccessCode = loginResource.addAccessCode(callback, "123");
    assert urlWithAccessCode.equals("http://host:8080/context/path?param1=value1&accessCode=123#fragment");
  }

  @Test
  public void testAccessCodeWithoutParametersAndFragment() throws Exception {
    LoginResource loginResource = new LoginResource();
    String callback = "http://host:8080/context/path#fragment";
    String urlWithAccessCode = loginResource.addAccessCode(callback, "123");
    assert urlWithAccessCode.equals("http://host:8080/context/path?accessCode=123#fragment");
  }

  @Test
  public void testRelativePathAndFragment() throws Exception {
    LoginResource loginResource = new LoginResource();
    String callback = "/context/path#fragment";
    String urlWithAccessCode = loginResource.addAccessCode(callback, "123");
    assert urlWithAccessCode.equals("/context/path?accessCode=123#fragment");
  }
}
