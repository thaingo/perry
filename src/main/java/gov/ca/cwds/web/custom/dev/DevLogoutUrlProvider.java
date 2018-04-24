package gov.ca.cwds.web.custom.dev;

import java.util.Optional;
import gov.ca.cwds.web.LogoutUrlProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevLogoutUrlProvider implements LogoutUrlProvider {

  @Override
  public Optional<String> apply(String callback) {
    return Optional.ofNullable(callback);
  }

}
