package gov.ca.cwds.config.mfa;

import gov.ca.cwds.web.LogoutUrlProvider;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mfa")
@Primary
public class MfaLogoutUrlProvider implements LogoutUrlProvider {

  @Override
  public Optional<String> apply(String callback) {
    return Optional.ofNullable(callback);
  }

}

