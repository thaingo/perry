package gov.ca.cwds.event;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Created by Alexander Serbin on 9/14/2018
 */
public class UserLoggedInEvent {

  private String userId;

  public UserLoggedInEvent(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
