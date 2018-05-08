package gov.ca.cwds.config.api.idm;

import gov.ca.cwds.config.api.common.BaseApiConfiguration;
import gov.ca.cwds.config.api.common.SpApiAuthenticationErrorHandler;
import gov.ca.cwds.config.api.common.SpApiAuthenticationProvider;
import gov.ca.cwds.config.api.common.SpApiSecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.annotation.RequestScope;


@Configuration
@Order(2)
@EnableWebSecurity
public class PerryIdmConfiguration extends BaseApiConfiguration {
  @Override
  protected String pattern() {
    return "/idm/**";
  }
}
