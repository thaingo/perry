package gov.ca.cwds.service.sso.custom;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class OAuth2RequestHttpEntityFactory {
  private Map<String, OAuth2RequestCustomizer> requestCustomizers;

  @Autowired
  public OAuth2RequestHttpEntityFactory(List<OAuth2RequestCustomizer> requestCustomizers) {
    this.requestCustomizers = requestCustomizers.stream()
        .collect(Collectors.toMap(customizer -> customizer.url, customizer -> customizer));
  }

  public HttpEntity build(String url, String accessToken) {
    Function<String, HttpEntity> entityProvider = requestCustomizers.get(url);
    if (entityProvider != null) {
      return entityProvider.apply(accessToken);
    }
    return null;
  }
}
