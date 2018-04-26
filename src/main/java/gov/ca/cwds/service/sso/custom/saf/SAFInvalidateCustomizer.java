package gov.ca.cwds.service.sso.custom.saf;

import gov.ca.cwds.service.sso.custom.OAuth2RequestCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@Profile("saf")
public class SAFInvalidateCustomizer extends OAuth2RequestCustomizer {

  @Autowired
  public SAFInvalidateCustomizer(@Value("${security.oauth2.resource.revokeTokenUri}") String url) {
    super(url);
  }

  @Override
  public HttpEntity apply(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("token", accessToken);
    params.add("token_type_hint", "access_token");
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return new HttpEntity<>(params, headers);
  }
}
