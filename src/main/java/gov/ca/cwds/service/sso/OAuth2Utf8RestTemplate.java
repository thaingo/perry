package gov.ca.cwds.service.sso;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

public class OAuth2Utf8RestTemplate extends OAuth2RestTemplate {

  public OAuth2Utf8RestTemplate(
      OAuth2ProtectedResourceDetails resource,
      OAuth2ClientContext context) {
    super(resource, context);
    setMessageConverters(getUtf8MessageConverters());
  }

  private List<HttpMessageConverter<?>> getUtf8MessageConverters() {
    return
        Collections.singletonList(new StringHttpMessageConverter(StandardCharsets.UTF_8));
  }
}
