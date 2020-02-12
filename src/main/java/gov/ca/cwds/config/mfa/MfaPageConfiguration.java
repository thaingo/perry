package gov.ca.cwds.config.mfa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.config.LoginServiceValidatorFilter;
import gov.ca.cwds.service.OauthLogoutHandler;
import gov.ca.cwds.service.mfa.CognitoResponseService;
import gov.ca.cwds.web.PerryLogoutSuccessHandler;

@Profile("mfa")
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties()
public class MfaPageConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private PerryProperties properties;

  @Autowired
  private MfaAuthenticationProvider authProvider;

  @Autowired
  private LoginServiceValidatorFilter loginServiceValidatorFilter;

  @Autowired
  private OauthLogoutHandler tokenRevocationLogoutHandler;

  @Autowired
  private PerryLogoutSuccessHandler perryLogoutSuccessHandler;

  @Autowired
  private CognitoResponseService cognitoResponseService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(authProvider);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //@formatter:off
    http
      .authorizeRequests().antMatchers("/authn/login").authenticated()
      .antMatchers(HttpMethod.TRACE, "/**").denyAll()
      .antMatchers("/**").permitAll()
      .and()
      .formLogin().loginPage(properties.getLoginPageUrl()).usernameParameter("CognitoResponse")
      .defaultSuccessUrl(properties.getHomePageUrl())
      .loginProcessingUrl("/login")
      .failureUrl("/error").and().logout()
      .logoutUrl("/authn/logout").permitAll()
      .addLogoutHandler(tokenRevocationLogoutHandler)
      .logoutSuccessHandler(perryLogoutSuccessHandler)
      .and().csrf().disable()
      .addFilterBefore(loginServiceValidatorFilter, UsernamePasswordAuthenticationFilter.class);
    //@formatter:on
  }

  @Bean
  @Primary
  @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.INTERFACES)
  public OAuth2ClientContext oAuth2ClientContext() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof OAuth2Authentication) {
      return cognitoResponseService.get((OAuth2Authentication) authentication);
    }
    return new DefaultOAuth2ClientContext();
  }

}
