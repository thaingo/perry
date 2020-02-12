package gov.ca.cwds.config.api.idm;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.EXTERNAL_APP;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import gov.ca.cwds.config.api.common.BaseApiConfiguration;

@Configuration
@Order(3)
@Profile("idm")
@EnableWebSecurity
public class PerryIdmConfiguration extends BaseApiConfiguration {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //@formatter:off
    http
        .antMatcher("/idm/**")
        .authorizeRequests()
        .antMatchers(HttpMethod.TRACE, "/**").denyAll()
        .anyRequest()
        .hasAnyAuthority(EXTERNAL_APP, SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN).and()
        .antMatcher("/idm/**").httpBasic();
    super.configure(http);
    //@formatter:on
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(getAuthProvider());
  }

  @Bean
  public AuthenticationProvider getAuthProvider() {
    return new IdmBasicAuthenticationProvider();
  }

}
