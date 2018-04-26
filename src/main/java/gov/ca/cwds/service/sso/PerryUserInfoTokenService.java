package gov.ca.cwds.service.sso;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
@Primary
public class PerryUserInfoTokenService extends UserInfoTokenServices {

  private PrincipalExtractor principalExtractor;
  private ResourceServerProperties resourceServerProperties;
  private SsoService oAuth2Service;

  @Autowired
  public PerryUserInfoTokenService(ResourceServerProperties resourceServerProperties) {
    super(resourceServerProperties.getUserInfoUri(), resourceServerProperties.getClientId());
    this.resourceServerProperties = resourceServerProperties;
  }

  @Override
  @SuppressWarnings("unchecked")
  public OAuth2Authentication loadAuthentication(String accessToken) {
    Map userInfo = oAuth2Service.getUserInfo(accessToken);
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
  public void setoAuth2Service(SsoService oAuth2Service) {
    this.oAuth2Service = oAuth2Service;
  }
}
