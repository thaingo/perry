package gov.ca.cwds.web.custom.dev;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import gov.ca.cwds.web.LogoutUrlProvider;
import gov.ca.cwds.web.error.AuthenticationEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevLogoutUrlProvider implements LogoutUrlProvider {

  private final static Log logger = LogFactory.getLog(DevLogoutUrlProvider.class);
  @Autowired(required = false)
  private HttpServletRequest request;

  @Override
  public Optional<String> apply(String callback) {
    logger.info("!!!REQUEST URL: " + request.getRequestURL());
    return Optional.ofNullable(callback);
  }

}
