package gov.ca.cwds.idm.event;

import gov.ca.cwds.event.UserLoggedInEvent;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.IdmService;
import gov.ca.cwds.idm.service.SearchService;
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
  private SearchService searchService;

  @Autowired
  private IdmService idmService;

  @EventListener
  public void handleUserLoggedInEvent(UserLoggedInEvent event) {
    try {
      LOGGER.info("Handling user logged in event for {}", event.getUserId());
      User user = idmService.findUser(event.getUserId());
      LOGGER.info("Last login timestamps for user {} is {}", event.getUserId(),
          user.getLastLoginDateTime());
      searchService.updateUser(user);
    } catch (Exception e) {
      LOGGER.error("Can't refresh user is users index", e);
    }
  }

}
