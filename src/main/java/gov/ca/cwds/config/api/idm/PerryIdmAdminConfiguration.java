package gov.ca.cwds.config.api.idm;

import gov.ca.cwds.config.api.common.BaseApiConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@Order(2)
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
