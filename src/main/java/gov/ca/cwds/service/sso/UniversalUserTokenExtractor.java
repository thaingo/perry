package gov.ca.cwds.service.sso;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.NsUserService;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by dmitry.rudenko on 7/28/2017.
 */
@Component
@Primary
@Profile({"prod"})
public class UniversalUserTokenExtractor implements PrincipalExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(UniversalUserTokenExtractor.class);

  public static final String USER_INFO_USERNAME_KEY = "Username";

  private PerryProperties configuration;

  private NsUserService nsUserService;

  @Override
  @SuppressWarnings("squid:S00112")
  public UniversalUserToken extractPrincipal(Map<String, Object> userInfo) {

    String username = (String) userInfo.get(USER_INFO_USERNAME_KEY);
    Optional<NsUser> optNsUser = nsUserService.findByUsername(username);

    NsUser nsUser = optNsUser.orElseGet(() -> {
      LOGGER.error(
          "Data for the user with username:{} is not found in NS database, user will not be authorized for any operation",
          username);
      return null;
    });

    try {
      UniversalUserToken userToken = configuration.getIdentityProvider().getIdpMapping()
          .map(userInfo, nsUser);
      userToken.setToken(generateToken());
      return userToken;
    } catch (ScriptException e) {
      throw new RuntimeException(e);
    }
  }

  private String generateToken() {
    return UUID.randomUUID().toString();
  }

  @Autowired
  public void setConfiguration(PerryProperties perryProperties) {
    this.configuration = perryProperties;
  }

  @Autowired
  public void setNsUserService(NsUserService nsUserService) {
    this.nsUserService = nsUserService;
  }
}
