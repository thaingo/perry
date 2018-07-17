package gov.ca.cwds.web;

import gov.ca.cwds.service.sso.custom.form.FormService;
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
  public FormService formService() {
    return Mockito.mock(FormService.class);
  }

}
