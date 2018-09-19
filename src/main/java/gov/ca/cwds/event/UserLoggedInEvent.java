package gov.ca.cwds.event;

import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Created by Alexander Serbin on 9/14/2018
 */
public class UserLoggedInEvent {

  private String userId;
  private LocalDateTime eventTimestamp;

  public UserLoggedInEvent(String userId, LocalDateTime eventTimestamp) {
    this.userId = userId;
    this.eventTimestamp = eventTimestamp;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public LocalDateTime getEventTimestamp() {
    return eventTimestamp;
  }

  public void setEventTimestamp(LocalDateTime eventTimestamp) {
    this.eventTimestamp = eventTimestamp;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
