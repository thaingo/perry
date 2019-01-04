package gov.ca.cwds.idm.event;

import gov.ca.cwds.idm.dto.User;
import java.time.LocalDateTime;

public class UserCreatedEvent {

  private LocalDateTime createdDateTime;
  private User user;

  public UserCreatedEvent(User user, LocalDateTime dateTime) {
    this.createdDateTime = dateTime;
    this.user = user;
  }

  public LocalDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public User getUser() {
    return user;
  }
}
