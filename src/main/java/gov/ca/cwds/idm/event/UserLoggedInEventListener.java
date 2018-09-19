package gov.ca.cwds.idm.event;

import gov.ca.cwds.event.UserLoggedInEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Created by Alexander Serbin on 9/14/2018
 */
@Component
@Profile("idm")
public class UserLoggedInEventListener {

  @EventListener
  public void handleUserLoggedInEvent(UserLoggedInEvent event) {
    //System.out.println(event);
  }

}
