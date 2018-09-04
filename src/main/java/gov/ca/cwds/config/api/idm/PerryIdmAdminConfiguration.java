package gov.ca.cwds.config.api.idm;

import gov.ca.cwds.config.LoggingFilter;
import gov.ca.cwds.config.api.common.BaseApiConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(2)
@Profile("idm")
@EnableWebSecurity
public class PerryIdmAdminConfiguration extends BaseApiConfiguration {

  @Autowired
  private LoggingFilter loggingFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/idm/permissions/**")
        .authorizeRequests()
        .anyRequest()
        .hasAnyAuthority("CARES-admin", "CWS-admin")
        .and().addFilterAfter(loggingFilter, UsernamePasswordAuthenticationFilter.class);
    super.configure(http);
  }
}
