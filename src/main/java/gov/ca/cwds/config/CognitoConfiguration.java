package gov.ca.cwds.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import gov.ca.cwds.service.oauth.CaresUserInfoTokenService;
import gov.ca.cwds.service.oauth.CognitoUserInfoTokenService;

@Profile({"cognito"})
@EnableOAuth2Sso
@Configuration
public class CognitoConfiguration extends OAuthConfiguration {

  @Bean
  @Primary
  @Autowired
  @Override
  public CaresUserInfoTokenService userInfoTokenServices(
      ResourceServerProperties resourceServerProperties) {
    return new CognitoUserInfoTokenService(resourceServerProperties);
  }

}
