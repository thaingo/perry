package gov.ca.cwds.config.mfa;

import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.service.mfa.CognitoResponseService;
import gov.ca.cwds.service.sso.PerryUserInfoTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

@Component
@Profile("mfa")
public class MfaAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private PerryUserInfoTokenService userInfoTokenService;
  @Autowired
  private CognitoResponseService cognitoResponseService;

  //must never throw an AuthenticationException!
  @Override
  public Authentication authenticate(Authentication authentication) {
    try {
      String cognitoResponseJson = authentication.getName();
      OAuth2ClientContext context = cognitoResponseService.convert(cognitoResponseJson);
      String accessToken = context.getAccessToken().getValue();
      OAuth2Authentication auth2Authentication = userInfoTokenService
          .loadAuthentication(accessToken);
      cognitoResponseService.put(context, auth2Authentication);
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