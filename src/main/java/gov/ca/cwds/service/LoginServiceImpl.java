package gov.ca.cwds.service;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import gov.ca.cwds.service.oauth.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    UniversalUserToken userToken = (UniversalUserToken) securityContext.getAuthentication().getPrincipal();
    String ssoToken = oAuth2Service.getSsoToken();
    String identity = identityMappingService.map(userToken, providerId);
    return tokenService.issueAccessCode(userToken, ssoToken, identity);
  }

  @Override
  public String issueToken(String accessCode) {
    return tokenService.getPerryTokenByAccessCode(accessCode);
  }

  @Override
  public String validate(String perryToken) {
    String currentSsoToken = oAuth2Service.validate();
    PerryTokenEntity perryTokenEntity = tokenService.getPerryToken(perryToken);
    if (!currentSsoToken.equals(perryTokenEntity.getSsoToken())) {
      tokenService.updateSsoToken(perryToken, currentSsoToken);
    }
    return perryTokenEntity.getJsonToken();
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
