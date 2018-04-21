package gov.ca.cwds.config;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import org.junit.Test;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import gov.ca.cwds.service.oauth.PerryUserInfoTokenService;

public class OauthConfigurationTest {
  @Test
  public void testUserInfoTokenServicesReturnsCaresUserInfoTokenService() {
    ResourceServerProperties resourceServerProperties = mock(ResourceServerProperties.class);
    OAuthConfiguration configuration = new OAuthConfiguration();

    PerryUserInfoTokenService service =
        configuration.userInfoTokenServices(resourceServerProperties);
    assertThat(service.getClass(), equalTo(PerryUserInfoTokenService.class));

  }
}
