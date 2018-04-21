package gov.ca.cwds.service.oauth.custom;

import java.util.function.Function;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;

public abstract class OAuth2RequestCustomizer implements Function<String, HttpEntity> {
  protected String url;
  @Autowired
  private OAuth2RequestHttpEntityFactory factory;

  public OAuth2RequestCustomizer(String url) {
    this.url = url;
  }

  @PostConstruct
  public void init() {
    factory.addHttpEntityProvider(this.url, this);
  }
}
