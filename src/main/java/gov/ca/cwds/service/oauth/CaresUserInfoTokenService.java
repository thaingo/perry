package gov.ca.cwds.service.oauth;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.http.HttpEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

public class CaresUserInfoTokenService extends UserInfoTokenServices {

  private PrincipalExtractor principalExtractor;
  private OAuth2RestTemplateService restClientService;
  private ResourceServerProperties resourceServerProperties;

  public CaresUserInfoTokenService(ResourceServerProperties resourceServerProperties) {
    super(resourceServerProperties.getUserInfoUri(), resourceServerProperties.getClientId());
    this.resourceServerProperties = resourceServerProperties;
  }

  @SuppressWarnings("rawtypes")
  protected HttpEntity httpEntityForGetUser(String accessToken) {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public OAuth2Authentication loadAuthentication(String accessToken) {

    @SuppressWarnings("rawtypes")
    Map userInfo = restClientService.restTemplate(accessToken).postForObject(
        resourceServerProperties.getUserInfoUri(), httpEntityForGetUser(accessToken), Map.class);
    Object principal = principalExtractor.extractPrincipal(userInfo);
    OAuth2Request request = new OAuth2Request(null, resourceServerProperties.getClientId(), null,
        true, null, null, null, null, null);
    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(principal, "N/A", extractAuthorities(userInfo));
    token.setDetails(userInfo);
    return new OAuth2Authentication(request, token);
  }

  private List<GrantedAuthority> extractAuthorities(@SuppressWarnings("rawtypes") Map map) {
    return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
  }

  @Override
  @Autowired
  public void setPrincipalExtractor(PrincipalExtractor principalExtractor) {
    this.principalExtractor = principalExtractor;
  }

  @Autowired
  public void setRestClientService(OAuth2RestTemplateService restClientService) {
    this.restClientService = restClientService;
  }
}
