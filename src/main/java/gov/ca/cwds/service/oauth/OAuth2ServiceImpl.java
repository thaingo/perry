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

  @Autowired
  public void setResourceServerProperties(ResourceServerProperties resourceServerProperties) {
    this.resourceServerProperties = resourceServerProperties;
  }

  @Autowired
  public void setResourceDetails(OAuth2ProtectedResourceDetails resourceDetails) {
    this.resourceDetails = resourceDetails;
  }

  @Override
  public Map getUserInfo(String accessToken) {
    return doPost(userRestTemplate(accessToken),
        resourceServerProperties.getUserInfoUri(),
        accessToken,
        Map.class);
  }

  @Override
  public OAuth2AccessToken validate() {
    doPost(userRestTemplate(), resourceServerProperties.getTokenInfoUri(), getAccessToken().getValue());
    return clientContext.getAccessToken();
  }

  @Override
  public void invalidate() {
    doPost(clientTemplate, revokeTokenUri, getAccessToken().getValue());
  }

  @Override
  public OAuth2AccessToken getAccessToken() {
    return clientContext.getAccessToken();
  }

  private OAuth2RestTemplate userRestTemplate() {
    return new OAuth2RestTemplate(resourceDetails, clientContext);
  }

  private OAuth2RestTemplate userRestTemplate(String accessToken) {
    return new OAuth2RestTemplate(resourceDetails,
        new DefaultOAuth2ClientContext(new DefaultOAuth2AccessToken(accessToken)));
  }

  private String doPost(OAuth2RestTemplate restTemplate, String url, String accessToken) {
    return restTemplate.postForObject(url,
        httpEntityFactory.build(url, accessToken),
        String.class);
  }

  private <T> T doPost(OAuth2RestTemplate restTemplate, String url, String accessToken, Class<T> clazz) {
    String response = doPost(restTemplate, url, accessToken);
    try {
      return objectMapper.readValue(response, clazz);
    } catch (IOException e) {
      throw new PerryException("url: " + url + ". error parsing response: " + response, e);
    }
  }
}
