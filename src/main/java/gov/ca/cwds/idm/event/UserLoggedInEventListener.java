package gov.ca.cwds.idm.event;

import gov.ca.cwds.event.UserLoggedInEvent;
import gov.ca.cwds.idm.service.UserLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Created by Alexander Serbin on 9/14/2018
 */
@Component
@Profile("idm")
public class UserLoggedInEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserLoggedInEventListener.class);

  @Autowired
  private UserLogService userLogService;

  @EventListener
  public void handleUserLoggedInEvent(UserLoggedInEvent event) {
    LOGGER.debug("Handling \"user logged in\" event for user {}", event.getUserId());
    userLogService.logUpdate(event.getUserId());
  }

}
