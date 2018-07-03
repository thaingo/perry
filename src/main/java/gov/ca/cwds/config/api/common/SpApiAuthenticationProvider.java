package gov.ca.cwds.config.api.common;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import gov.ca.cwds.service.LoginService;
import gov.ca.cwds.service.TokenService;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SpApiAuthenticationProvider implements AuthenticationProvider {
  @Autowired
  private TokenService tokenService;
  @Autowired
  private LoginService loginService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    try {
      String perryToken = (String) authentication.getPrincipal();
      UniversalUserToken universalUserToken = loginService.validate(perryToken);
      PerryTokenEntity perryTokenEntity = tokenService.getPerryToken(perryToken);
      List<GrantedAuthority> roles = universalUserToken.getRoles().stream()
          .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
      roles.add(new SimpleGrantedAuthority("SP_API_CLIENT"));
      PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(
          universalUserToken,
          SerializationUtils.deserialize(perryTokenEntity.getSecurityContext()),
          roles);
      //TODO : remove
      authenticationToken.setDetails(perryTokenEntity);
      return authenticationToken;
    } catch (Exception e) {
      throw new BadCredentialsException("invalid token", e);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.isAssignableFrom(PreAuthenticatedAuthenticationToken.class);
  }
}
