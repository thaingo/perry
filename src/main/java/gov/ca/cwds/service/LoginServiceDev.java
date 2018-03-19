package gov.ca.cwds.service;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.config.Constants;
import gov.ca.cwds.rest.api.domain.PerryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static gov.ca.cwds.config.Constants.IDENTITY;

/**
 * Created by TPT2 on 10/30/2017.
 */
@Service
@Profile("dev")
public class LoginServiceDev implements LoginService {
  private TokenService tokenService;

  @Override
  public String issueAccessCode(String providerId) {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("test");
    accessToken.setAdditionalInformation(new HashMap<>());
    UniversalUserToken userToken = (UniversalUserToken) securityContext.getAuthentication().getPrincipal();
    accessToken.getAdditionalInformation().put(Constants.IDENTITY, userToken.getParameter(IDENTITY));
    return tokenService.issueAccessCode(userToken, accessToken);
  }

  @Override
  public String issueToken(String accessCode) {
    return tokenService.getPerryTokenByAccessCode(accessCode);
  }

  @Override
  public String validate(String perryToken) {
    OAuth2AccessToken accessToken = tokenService.getAccessTokenByPerryToken(perryToken);
    if (accessToken == null) {
      throw new PerryException("invalid token");
    }
    return (String) accessToken.getAdditionalInformation().get(IDENTITY);
  }

  @Override
  public void invalidate(String perryToken) {
    tokenService.deleteToken(perryToken);
  }

  @Autowired
  public void setTokenService(TokenService tokenService) {
    this.tokenService = tokenService;
  }
}