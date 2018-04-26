package gov.ca.cwds.service;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.config.Constants;
import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
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
    UniversalUserToken userToken = (UniversalUserToken) securityContext.getAuthentication().getPrincipal();
    return tokenService.issueAccessCode(userToken, userToken.getToken(), (String)userToken.getParameter(IDENTITY));
  }

  @Override
  public String issueToken(String accessCode) {
    return tokenService.getPerryTokenByAccessCode(accessCode);
  }

  @Override
  public String validate(String token) {
    PerryTokenEntity perryToken = tokenService.getPerryToken(token);
    if (perryToken == null) {
      throw new PerryException("invalid token");
    }
    return perryToken.getJsonToken();
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