package gov.ca.cwds.service.oauth.custom;

import java.util.Arrays;
import java.util.Collections;
import gov.ca.cwds.service.oauth.custom.cognito.CognitoInvalidateCustomizer;
import gov.ca.cwds.service.oauth.custom.cognito.CognitoUserInfoCustomizer;
import gov.ca.cwds.service.oauth.custom.cognito.CognitoUserPoolRequest;
import gov.ca.cwds.service.oauth.custom.saf.SAFInvalidateCustomizer;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

public class OAuth2RequestHttpEntityFactoryTest {
  private static final String ACCESS_TOKEN = "access_token";
  private OAuth2RequestHttpEntityFactory factory;

  @Before
  public void init() {
    factory = new OAuth2RequestHttpEntityFactory(
        Arrays.asList(
            new CognitoUserInfoCustomizer(CognitoUserInfoCustomizer.class.getName()),
            new CognitoInvalidateCustomizer(CognitoInvalidateCustomizer.class.getName()),
            new SAFInvalidateCustomizer(SAFInvalidateCustomizer.class.getName())
        )
    );
  }

  @Test
  public void testCognitoUserInfoCustomizer() throws Exception {
    HttpEntity httpEntity = factory.build(CognitoUserInfoCustomizer.class.getName(), ACCESS_TOKEN);
    Assert.assertEquals(httpEntity.getHeaders().get("Content-Type"),
        Collections.singletonList("application/x-amz-json-1.1"));
    Assert.assertEquals(httpEntity.getHeaders().get("X-Amz-Target"),
        Collections.singletonList("AWSCognitoIdentityProviderService.GetUser"));
    CognitoUserPoolRequest request = new CognitoUserPoolRequest();
    request.setAccessToken(ACCESS_TOKEN);
    Assert.assertEquals(httpEntity.getBody(), new ObjectMapper().writeValueAsString(request));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testCognitoInvalidateCustomizer() {
    factory.build(CognitoInvalidateCustomizer.class.getName(), ACCESS_TOKEN);
  }

  @Test
  public void testSAFInvalidateCustomizer() {
    HttpEntity httpEntity = factory.build(SAFInvalidateCustomizer.class.getName(), ACCESS_TOKEN);
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("token", ACCESS_TOKEN);
    params.add("token_type_hint", "access_token");
    Assert.assertEquals(params, httpEntity.getBody());
    Assert.assertEquals(httpEntity.getHeaders().get(HttpHeaders.CONTENT_TYPE),
        Collections.singletonList(APPLICATION_FORM_URLENCODED_VALUE));
  }
}
