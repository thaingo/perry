package gov.ca.cwds.config.api.idm;

import gov.ca.cwds.config.api.common.BaseApiConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@Configuration
@Order(2)
@EnableWebSecurity
public class PerryIdmConfiguration extends BaseApiConfiguration {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/idm/**").authorizeRequests().anyRequest().hasAuthority("CWS-admin");
    http.antMatcher("/admin/**").authorizeRequests().anyRequest().hasAuthority("CAP-admin");
    super.configure(http);
  }
}
