package gov.ca.cwds.service.oauth.custom;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class OAuth2RequestHttpEntityFactory {
  private Map<String, Function<String, HttpEntity>> entityProviders = new HashMap<>();

  void addHttpEntityProvider(String url, Function<String, HttpEntity> httpEntityProvider) {
    entityProviders.put(url, httpEntityProvider);
  }

  public HttpEntity build(String url, String accessToken) {
    Function<String, HttpEntity> entityProvider = entityProviders.get(url);
    if (entityProvider != null) {
      return entityProvider.apply(accessToken);
    }
    return null;
  }
}
