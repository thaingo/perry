package gov.ca.cwds.config.mfa;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.service.sso.PerryUserInfoTokenService;
import java.util.Date;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

@Component
@Profile("mfa")
@SuppressWarnings("unchecked")
public class MfaAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  PerryProperties perryProperties;
  @Autowired
  PerryUserInfoTokenService userInfoTokenService;

  private ObjectMapper objectMapper = new ObjectMapper();


  private static <T> T get(Map map, Class<T> clazz, String... path) {
    Map node = map;
    for (int i = 0; i < path.length - 1; i++) {
      node = (Map) node.get(path[i]);
    }
    return (T) node.get(path[path.length - 1]);
  }

  //must never throw an AuthenticationException!
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    try {
      String cognitoResponseJson = authentication.getName();
      Map cognitoResponseMap = objectMapper.readValue(cognitoResponseJson, Map.class);
      String accessToken = get(cognitoResponseMap, String.class, "accessToken", "jwtToken");
      String refreshToken = get(cognitoResponseMap, String.class, "refreshToken", "token");
      Integer exp = get(cognitoResponseMap, Integer.class, "accessToken", "payload", "exp");
      OAuth2Authentication auth2Authentication =
          userInfoTokenService.loadAuthentication(accessToken);
      Map details = (Map) auth2Authentication.getUserAuthentication().getDetails();
      details.put(MfaPageConfiguration.ACCESS_TOKEN_DETAIL_NAME, accessToken);
      details.put(MfaPageConfiguration.REFRESH_TOKEN_DETAIL_NAME, refreshToken);
      //TODO exp fix
      details.put(MfaPageConfiguration.EXP_DETAIL_NAME, new Date(exp * 1000L));
      return auth2Authentication;
    } catch (Exception e) {
      throw new PerryException("COGNITO RESPONSE PROCESSING ERROR", e);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(
        UsernamePasswordAuthenticationToken.class);
  }

}