package gov.ca.cwds.service.oauth;

import java.util.Map;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public interface OAuth2Service {
  Map getUserInfo(String accessToken);
  String validate();
  void invalidate();
  String getSsoToken();
}
