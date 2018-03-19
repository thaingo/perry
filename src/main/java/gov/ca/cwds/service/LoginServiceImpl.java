package gov.ca.cwds.service;

import static gov.ca.cwds.config.Constants.IDENTITY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.config.Constants;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.service.oauth.OAuth2RestTemplateService;

/**
 * Created by TPT2 on 10/24/2017.
 */
@Service
@Profile("prod")
public class LoginServiceImpl implements LoginService {
  @Value("${security.oauth2.resource.revokeTokenUri}")
  private String revokeTokenUri;
  @Autowired(required = false)
  private OAuth2ClientContext clientContext;

  private ResourceServerProperties resourceServerProperties;
  private IdentityMappingService identityMappingService;
  private TokenService tokenService;
  private OAuth2RestTemplateService restClientService;

  @Override
  public String issueAccessCode(String providerId) {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    OAuth2Authentication authentication =
        (OAuth2Authentication) securityContext.getAuthentication();
    UniversalUserToken userToken = (UniversalUserToken) authentication.getPrincipal();
    OAuth2AccessToken accessToken = clientContext.getAccessToken();
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
    OAuth2AccessToken accessToken = tokenService.getAccessTokenByPerryToken(perryToken);
    OAuth2RestTemplate restTemplate = restClientService.restTemplate(accessToken);
    restTemplate.postForObject(resourceServerProperties.getTokenInfoUri(),
        httpEntityForValidation(accessToken), String.class);

    String identity = (String) accessToken.getAdditionalInformation().get(IDENTITY);
    OAuth2AccessToken reissuedAccessToken = restTemplate.getOAuth2ClientContext().getAccessToken();
    if (reissuedAccessToken != accessToken) {
      reissuedAccessToken.getAdditionalInformation().put(IDENTITY, identity);
      tokenService.updateAccessToken(perryToken, reissuedAccessToken);
    }
    return identity;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void invalidate(String perryToken) {
    OAuth2AccessToken accessToken = tokenService.deleteToken(perryToken);
    try {
      HttpEntity request = httpEntityForInvalidation(accessToken);
      restClientService.clientRestTemplate().postForEntity(revokeTokenUri, request, String.class)
          .getBody();
    } catch (Exception e) {
      throw new PerryException("Token Revocation problem for revokeTokenUri = " + revokeTokenUri,
          e);
    }
  }

  @SuppressWarnings("rawtypes")
  protected HttpEntity httpEntityForValidation(OAuth2AccessToken accessToken) {
    return null;
  }


  @SuppressWarnings("rawtypes")
  protected HttpEntity httpEntityForInvalidation(OAuth2AccessToken accessToken) {
    HttpHeaders headers = new HttpHeaders();
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("token", accessToken.getValue());
    params.add("token_type_hint", "access_token");
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return new HttpEntity<>(params, headers);
  }

  @Autowired
  public void setResourceServerProperties(ResourceServerProperties resourceServerProperties) {
    this.resourceServerProperties = resourceServerProperties;
  }

  @Autowired
  public void setIdentityMappingService(IdentityMappingService identityMappingService) {
    this.identityMappingService = identityMappingService;
  }

  @Autowired
  public void setRestClientService(OAuth2RestTemplateService restClientService) {
    this.restClientService = restClientService;
  }

  @Autowired
  public void setTokenService(TokenService tokenService) {
    this.tokenService = tokenService;
  }
}
