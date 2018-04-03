package gov.ca.cwds.service.http;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class CognitoHeadersTest {

  private CognitoHeaders cognitoHeaders;

  private final static String targetHeaderName = "X-Amz-Target";

  private String apiVersion = "someversion";
  private String authorization = "someauthorization";
  private String target = "sometarget";

  @Before
  public void before() {
    cognitoHeaders = new CognitoHeaders();
    cognitoHeaders.setApiVersion(apiVersion);
    cognitoHeaders.setAuthorization(authorization);
  }

  @Test
  public void testGetHeadersForApiCallHasCorrectContentType() {
    HttpHeaders headers = cognitoHeaders.getHeadersForApiCall("sometarget");
    assertThat(headers.getContentType(), equalTo(new MediaType("application", apiVersion)));
  }

  @Test
  public void testGetHeadersForApiCallHasCorrectTarget() {
    HttpHeaders headers = cognitoHeaders.getHeadersForApiCall(target);
    assertThat(headers.get(targetHeaderName).size(), equalTo(1));
    assertThat(headers.get(targetHeaderName).get(0), equalTo(target));
  }

  @Test
  public void testGetHeadersForApplicationFormUrlEncodedHasCorrectContentType() {
    HttpHeaders headers = cognitoHeaders.getHeadersForApplicationFormUrlEncoded();
    assertThat(headers.getContentType(), equalTo(MediaType.APPLICATION_FORM_URLENCODED));
  }

  @Test
  public void testGetHeadersForApplicationFormUrlEncodedHasCorrectAuthorization() {
    HttpHeaders headers = cognitoHeaders.getHeadersForApplicationFormUrlEncoded();
    assertThat(headers.get("Authorization").size(), equalTo(1));
    assertThat(headers.get("Authorization").get(0), equalTo("Basic " + authorization));
  }
}
