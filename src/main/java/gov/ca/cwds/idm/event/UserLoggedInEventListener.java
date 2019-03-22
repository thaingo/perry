package gov.ca.cwds.idm.event;

import gov.ca.cwds.event.UserLoggedInEvent;
import gov.ca.cwds.idm.service.IdmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("idm")
public class UserLoggedInEventListener {

  @Autowired
  private IdmService idmService;

  @EventListener
  public void handleUserLoggedInEvent(UserLoggedInEvent event) {
    idmService.saveLastLoginTime(event.getUserId(), event.getLoginTime());
  }
}
