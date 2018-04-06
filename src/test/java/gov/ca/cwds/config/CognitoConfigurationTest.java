package gov.ca.cwds.config;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import org.junit.Test;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import gov.ca.cwds.service.oauth.CaresUserInfoTokenService;
import gov.ca.cwds.service.oauth.CognitoUserInfoTokenService;

public class CognitoConfigurationTest {

  @Test
  public void testUserInfoTokenServicesReturnsCaresUserInfoTokenService() {
    ResourceServerProperties resourceServerProperties = mock(ResourceServerProperties.class);
    CognitoConfiguration configuration = new CognitoConfiguration();

    CaresUserInfoTokenService service =
        configuration.userInfoTokenServices(resourceServerProperties);
    assertThat(service.getClass(), equalTo(CognitoUserInfoTokenService.class));

  }

}
