package gov.ca.cwds.service;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.config.Constants;
import gov.ca.cwds.service.oauth.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;


import static gov.ca.cwds.config.Constants.IDENTITY;

/**
 * Created by TPT2 on 10/24/2017.
 */
@Service
@Profile("prod")
public class LoginServiceImpl implements LoginService {

  private IdentityMappingService identityMappingService;
  private TokenService tokenService;
  private OAuth2Service oAuth2Service;

  @Override
  public String issueAccessCode(String providerId) {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    OAuth2Authentication authentication =
        (OAuth2Authentication) securityContext.getAuthentication();
    UniversalUserToken userToken = (UniversalUserToken) authentication.getPrincipal();
    OAuth2AccessToken accessToken = oAuth2Service.getAccessToken();
    String identity = identityMappingService.map(userToken, providerId);
    accessToken.getAdditionalInformation().put(Constants.IDENTITY, identity);
    return tokenService.issueAccessCode(userToken, accessToken);
  }

  @Override
  public String issueToken(String accessCode) {
    return tokenService.getPerryTokenByAccessCode(accessCode);
  }

  @Override
  public String validate(String perryToken) {
    OAuth2AccessToken currentAccessToken = oAuth2Service.validate();
    OAuth2AccessToken persistentAccessToken = tokenService.getAccessTokenByPerryToken(perryToken);
    String identity = (String) persistentAccessToken.getAdditionalInformation().get(IDENTITY);
    if (!currentAccessToken.getValue().equals(persistentAccessToken.getValue())) {
      currentAccessToken.getAdditionalInformation().put(IDENTITY, identity);
      tokenService.updateAccessToken(perryToken, currentAccessToken);
    }
    return identity;
  }

  @Override
  public void invalidate(String perryToken) {
    tokenService.deleteToken(perryToken);
    oAuth2Service.invalidate();
  }

  @Autowired
  public void setIdentityMappingService(IdentityMappingService identityMappingService) {
    this.identityMappingService = identityMappingService;
  }

  @Autowired
  public void setTokenService(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Autowired
  public void setoAuth2Service(OAuth2Service oAuth2Service) {
    this.oAuth2Service = oAuth2Service;
  }
}
