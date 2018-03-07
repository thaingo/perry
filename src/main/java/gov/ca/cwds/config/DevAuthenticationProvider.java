package gov.ca.cwds.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.UniversalUserToken;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static gov.ca.cwds.config.Constants.IDENTITY;

/**
 * username format: user:role1,role2
 * password can by any
 */
@Profile("dev")
@Component
public class DevAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  PerryProperties perryProperties;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    tryAuthenticate(authentication);
    String json = authentication.getName();
    String userName = getUserName(json);
    UniversalUserToken userToken = new UniversalUserToken();
    userToken.setToken(UUID.randomUUID().toString());
    userToken.setUserId(userName);
    userToken.setParameter(IDENTITY, json);
    return new UsernamePasswordAuthenticationToken(
            userToken, "N/A", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
  }

  private String getUserName(String json) {
    try {
      return objectMapper.readTree(json).get("user").getTextValue();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(
            UsernamePasswordAuthenticationToken.class);
  }

  @SuppressFBWarnings("PATH_TRAVERSAL_IN") //user file location taken from property file only!
  private void tryAuthenticate(Authentication authentication) throws AuthenticationException {
    try {
      if (!StringUtils.isEmpty(perryProperties.getUsers())) {
        String user = authentication.getName();
        String password = authentication.getCredentials().toString();
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(perryProperties.getUsers())));
        if (!properties.containsKey(user) || !properties.getProperty(user).equals(password)) {
          throw new BadCredentialsException("Authentication failed");
        }
      }
    } catch (IOException e) {
      throw new AuthenticationServiceException("Can't read users", e);
    }
  }
}