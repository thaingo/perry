package gov.ca.cwds.config.mfa;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.config.LoginServiceValidatorFilter;
import gov.ca.cwds.service.OauthLogoutHandler;
import gov.ca.cwds.service.sso.custom.form.DevAuthenticationProvider;
import gov.ca.cwds.web.PerryLogoutSuccessHandler;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Profile("mfa")
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties()
public class MfaPageConfiguration extends WebSecurityConfigurerAdapter {
  public static final String ACCESS_TOKEN_DETAIL_NAME = "accessToken";
  public static final String REFRESH_TOKEN_DETAIL_NAME = "refreshToken";
  public static final String EXP_DETAIL_NAME = "exp";

  @Autowired
  private MfaAuthenticationProvider authProvider;

  @Autowired
  private LoginServiceValidatorFilter loginServiceValidatorFilter;

  @Autowired
  private OauthLogoutHandler tokenRevocationLogoutHandler;

  @Autowired
  private PerryLogoutSuccessHandler perryLogoutSuccessHandler;

  @Autowired
  PerryProperties properties;

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
        //TODO
        .failureUrl("/error")
        .and()
        .logout()
        .logoutUrl("/authn/logout").permitAll()
        .addLogoutHandler(tokenRevocationLogoutHandler)
        .logoutSuccessHandler(perryLogoutSuccessHandler)
        .and().csrf().disable()
        .addFilterBefore(loginServiceValidatorFilter, UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  @Primary
  @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
  public OAuth2ClientContext oAuth2ClientContext() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    OAuth2ClientContext context = new DefaultOAuth2ClientContext();
    if(authentication instanceof OAuth2Authentication) {
      Map details = (Map) ((OAuth2Authentication)authentication)
          .getUserAuthentication().getDetails();
      String accessToken = (String) details.get(MfaPageConfiguration.ACCESS_TOKEN_DETAIL_NAME);
      Date exp = (Date) details.get(MfaPageConfiguration.EXP_DETAIL_NAME);
      String refreshToken = (String) details.get(MfaPageConfiguration.REFRESH_TOKEN_DETAIL_NAME);
      DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
      oAuth2AccessToken.setExpiration(exp);
      OAuth2RefreshToken oAuth2RefreshToken = new DefaultOAuth2RefreshToken(refreshToken);
      oAuth2AccessToken.setRefreshToken(oAuth2RefreshToken);
      context.setAccessToken(new DefaultOAuth2AccessToken(oAuth2AccessToken));
      return context;
    }
    return new DefaultOAuth2ClientContext();
  }
}