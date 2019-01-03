package gov.ca.cwds.idm.event;

import gov.ca.cwds.idm.dto.User;
import java.time.LocalDateTime;

public class UserCreatedEvent {

  private String userId;
  private LocalDateTime createdDateTime;
  private User user;

  public UserCreatedEvent(String userId, User user, LocalDateTime dateTime) {
    this.userId = userId;
    this.createdDateTime = dateTime;
    this.user = user;
  }

  public String getUserId() {
    return userId;
  }

  public LocalDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public User getUser() {
    return user;
  }
}
