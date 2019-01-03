package gov.ca.cwds.idm.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("idm")
public class ChangeLogEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChangeLogEventListener.class);

  @EventListener
  //@Async
  public void handleChangeLogEvent(UserCreatedEvent event) {
    LOGGER.info("User created " + event);

  }

}
