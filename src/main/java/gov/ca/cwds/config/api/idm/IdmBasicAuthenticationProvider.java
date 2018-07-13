package gov.ca.cwds.config.api.idm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

public class IdmBasicAuthenticationProvider implements AuthenticationProvider {
  @Value("${perry.identityManager.idmBasicAuthUser:user}")
  private String idmBasicAuthUser;

  @Value("${perry.identityManager.idmBasicAuthPass:blabla}")
  private String idmBasicAuthPass;

  @Override
  public Authentication authenticate(Authentication auth) {
    if (auth.isAuthenticated()) {
      return auth;
    }
    if (idmBasicAuthPass == null || idmBasicAuthUser == null || auth.getCredentials() == null) {
      return auth;
    }
    String username = auth.getName();
    String password = auth.getCredentials().toString();

    if (idmBasicAuthUser.equals(username) && idmBasicAuthPass.equals(password)) {
      return new UsernamePasswordAuthenticationToken(
          auth.getPrincipal(),
          "",
          Collections.singletonList(new SimpleGrantedAuthority("IDM-job")));
    }
    return auth;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
  }
}
