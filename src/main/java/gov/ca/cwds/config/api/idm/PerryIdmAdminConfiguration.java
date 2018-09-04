package gov.ca.cwds.config.api.idm;

import gov.ca.cwds.config.api.common.BaseApiConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@Order(2)
@Profile("idm")
@EnableWebSecurity
public class PerryIdmAdminConfiguration extends BaseApiConfiguration {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/idm/permissions/**")
        .authorizeRequests()
        .anyRequest()
        .hasAnyAuthority("CARES-admin", "CWS-admin");
    super.configure(http);
  }
}
