package gov.ca.cwds.web.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

/**
 * Created by TPT2 on 1/3/2018.
 */
@Component
public class AuthenticationEventListener implements ApplicationListener<AbstractAuthenticationEvent> {
  private static final Log logger = LogFactory.getLog(AuthenticationEventListener.class);

  @Override
  public void onApplicationEvent(AbstractAuthenticationEvent event) {
    if(event instanceof AbstractAuthenticationFailureEvent) {
      AbstractAuthenticationFailureEvent failureEvent = (AbstractAuthenticationFailureEvent) event;
      logger.error("An exception occurred during OAuth2 authentication", failureEvent.getException());
    }
  }
}
