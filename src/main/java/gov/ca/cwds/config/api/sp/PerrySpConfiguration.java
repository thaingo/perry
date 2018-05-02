package gov.ca.cwds.config.api.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.annotation.RequestScope;


@Configuration
@Order(1)
@EnableWebSecurity
public class PerrySpConfiguration extends WebSecurityConfigurerAdapter {
  @Autowired
  private SpApiSecurityFilter filter;
  @Autowired
  private SpApiAuthenticationErrorHandler errorHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/authn/validate*").authorizeRequests().anyRequest().authenticated()
        .and().addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling().authenticationEntryPoint(errorHandler);

  }
}
