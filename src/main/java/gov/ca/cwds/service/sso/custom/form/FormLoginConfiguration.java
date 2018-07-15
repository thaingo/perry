package gov.ca.cwds.service.sso.custom.form;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.config.LoginServiceValidatorFilter;
import gov.ca.cwds.web.PerryLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by dmitry.rudenko on 5/23/2017.
 */
@Profile("dev")
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties()
public class FormLoginConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  PerryProperties properties;
  @Autowired
  private DevAuthenticationProvider authProvider;
  @Autowired
  private LoginServiceValidatorFilter loginServiceValidatorFilter;
  @Autowired
  private PerryLogoutSuccessHandler perryLogoutSuccessHandler;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(authProvider);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/authn/login").authenticated()
        .antMatchers("/**").permitAll()
        .and()
        .formLogin()
        .loginPage(properties.getLoginPageUrl())
        .usernameParameter("CognitoResponse")
        .defaultSuccessUrl(properties.getHomePageUrl())
        .loginProcessingUrl("/login")
        .failureUrl("/login.html?error=true")
        .and()
        .logout().logoutUrl("/authn/logout").permitAll()
        .logoutSuccessHandler(perryLogoutSuccessHandler)
        .and().csrf().disable()
        .addFilterBefore(loginServiceValidatorFilter, UsernamePasswordAuthenticationFilter.class);
  }
}