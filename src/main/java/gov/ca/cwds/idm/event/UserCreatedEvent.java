package gov.ca.cwds.idm.event;

import gov.ca.cwds.idm.dto.User;
import java.time.LocalDateTime;

public class UserCreatedEvent {

  private LocalDateTime eventDateTime;
  private User user;

  public UserCreatedEvent(User user) {
    this.eventDateTime = LocalDateTime.now();
    this.user = user;
  }

  public LocalDateTime getEventDateTime() {
    return eventDateTime;
  }

  public User getUser() {
    return user;
  }
}
