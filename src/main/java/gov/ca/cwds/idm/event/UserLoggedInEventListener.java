package gov.ca.cwds.idm.event;

import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_WRITE_LAST_LOGIN_TIME;

import gov.ca.cwds.event.UserLoggedInEvent;
import gov.ca.cwds.idm.service.UserNsService;
import gov.ca.cwds.service.messages.MessagesService;
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
  private UserNsService userNsService;

  @Autowired
  private MessagesService messagesService;

  @EventListener
  public void handleUserLoggedInEvent(UserLoggedInEvent event) {
    String userId = event.getUserId();

    try {
      if (userId == null) {
        LOGGER.warn("userToken doesn't contain the userId, no following actions expected}");
        return;
      }
      LOGGER.debug("Handling \"user logged in\" event for user {}", userId);
      userNsService.saveLastLoginTime(userId, event.getLoginTime());

    } catch (Exception e) {
      String msg = messagesService.getTechMessage(UNABLE_TO_WRITE_LAST_LOGIN_TIME, userId);
      LOGGER.error(msg, e);
    }
  }
}
