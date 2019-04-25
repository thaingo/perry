package gov.ca.cwds.service.sso;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.test.web.client.MockRestServiceServer;


public class OAuth2Utf8RestTemplateTest {

  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  @Test
  public void templateTest() throws IOException, URISyntaxException {
    UserInfo userInfo = new UserInfo("Gregé", "Modrič");

    String detailsString =
        MAPPER.writeValueAsString(userInfo);

    OAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
    DefaultOAuth2ClientContext context = new DefaultOAuth2ClientContext();

    OAuth2Utf8RestTemplate template = new OAuth2Utf8RestTemplate(resource, context);

    template.setAccessTokenProvider(new StubAccessTokenProvider());

    MockRestServiceServer mockServer =
        MockRestServiceServer.bindTo(template).build();
    mockServer.expect(requestTo(any(String.class)))
        .andRespond(withSuccess(detailsString, MediaType.APPLICATION_JSON));

    String response = template.postForObject("anyURI", HttpEntity.EMPTY, String.class);

    assertEquals("{\"firstName\":\"Gregé\",\"lastName\":\"Modrič\"}", response);

  }

  private class UserInfo {

    UserInfo(String firstName, String lastName) {
      this.firstName = firstName;
      this.lastName = lastName;
    }

    private String firstName;
    private String lastName;

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
  }

  private static class StubAccessTokenProvider implements AccessTokenProvider {

    public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details,
        AccessTokenRequest parameters)
        throws UserRedirectRequiredException {
      return new DefaultOAuth2AccessToken("FOO");
    }

    public boolean supportsRefresh(OAuth2ProtectedResourceDetails resource) {
      return false;
    }

    public OAuth2AccessToken refreshAccessToken(OAuth2ProtectedResourceDetails resource,
        OAuth2RefreshToken refreshToken, AccessTokenRequest request)
        throws UserRedirectRequiredException {
      return null;
    }

    public boolean supportsResource(OAuth2ProtectedResourceDetails resource) {
      return true;
    }
  }

}