package gov.ca.cwds.service.oauth;

import java.io.IOException;
import java.util.Map;
import javax.annotation.PostConstruct;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.service.oauth.custom.OAuth2RequestHttpEntityFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class OAuth2ServiceImpl implements OAuth2Service {
  private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2ServiceImpl.class);

  private OAuth2ProtectedResourceDetails resourceDetails;
  private OAuth2RestTemplate clientTemplate;
  private ResourceServerProperties resourceServerProperties;
  @Autowired(required = false)
  private OAuth2ClientContext clientContext;
  @Value("${security.oauth2.resource.revokeTokenUri}")
  private String revokeTokenUri;
  @Autowired
  private OAuth2RequestHttpEntityFactory httpEntityFactory;
  private ObjectMapper objectMapper;


  @PostConstruct
  public void init() {
    ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
    resource.setAccessTokenUri(resourceDetails.getAccessTokenUri());
    resource.setClientId(resourceServerProperties.getClientId());
    resource.setClientSecret(resourceServerProperties.getClientSecret());
    resource.setAuthenticationScheme(resourceDetails.getAuthenticationScheme());
    resource.setClientAuthenticationScheme(resourceDetails.getClientAuthenticationScheme());
    clientTemplate = new OAuth2RestTemplate(resource);
    objectMapper = new ObjectMapper();
  }

  protected OAuth2RestTemplate userRestTemplate() {
    return new OAuth2RestTemplate(resourceDetails, clientContext);
  }

  protected OAuth2RestTemplate userRestTemplate(String accessToken) {
    return new OAuth2RestTemplate(resourceDetails,
        new DefaultOAuth2ClientContext(new DefaultOAuth2AccessToken(accessToken)));
  }

  @Autowired
  public void setResourceServerProperties(ResourceServerProperties resourceServerProperties) {
    this.resourceServerProperties = resourceServerProperties;
  }

  @Autowired
  public void setResourceDetails(OAuth2ProtectedResourceDetails resourceDetails) {
    this.resourceDetails = resourceDetails;
  }

  @Override
  public Map getUserInfo() {
    return getUserInfo(getAccessToken().getValue());
  }

  @Override
  public Map getUserInfo(String accessToken) {
    try {
      String response = userRestTemplate(accessToken)
          .postForObject(resourceServerProperties.getUserInfoUri(),
              httpEntityFactory.build(resourceServerProperties.getUserInfoUri(), accessToken),
              String.class);
      return objectMapper.readValue(response, Map.class);
    } catch (IOException e) {
      throw new PerryException("user info access error", e);
    }
  }

  @Override
  public OAuth2AccessToken validate() {
    OAuth2RestTemplate restTemplate = userRestTemplate();
    restTemplate.postForObject(resourceServerProperties.getTokenInfoUri(),
        httpEntityFactory.build(resourceServerProperties.getTokenInfoUri(), getAccessToken().getValue()),
        String.class);
    return clientContext.getAccessToken();
  }

  @Override
  public void invalidate() {
    try {
      clientTemplate.postForEntity(revokeTokenUri,
          httpEntityFactory.build(revokeTokenUri, getAccessToken().getValue()),
          String.class);
    } catch (UnsupportedOperationException e) {
      LOGGER.error("Invalidation problems: ", e);
    }
  }

  @Override
  public OAuth2AccessToken getAccessToken() {
    return clientContext.getAccessToken();
  }
}
