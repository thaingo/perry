package gov.ca.cwds.config.api.sp;

import gov.ca.cwds.config.api.common.BaseApiConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@Configuration
@Order(1)
@EnableWebSecurity
public class PerrySpConfiguration extends BaseApiConfiguration {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/authn/validate").authorizeRequests().anyRequest().authenticated();
    super.configure(http);
  }
}
