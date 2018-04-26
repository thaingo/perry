package gov.ca.cwds.service.sso.custom;

import java.util.function.Function;
import org.springframework.http.HttpEntity;

public abstract class OAuth2RequestCustomizer implements Function<String, HttpEntity> {
  protected String url;

  public OAuth2RequestCustomizer(String url) {
    this.url = url;
  }

}
