package gov.ca.cwds.web;

import gov.ca.cwds.service.sso.OAuth2Service;
import gov.ca.cwds.service.sso.custom.form.FormService;
import java.util.Map;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class PerryLoginTestConfiguration {

  @Bean
  @Primary
  public OAuth2Service oAuth2Service() {
    return Mockito.spy(MockOAuth2Service.class);
  }

}
