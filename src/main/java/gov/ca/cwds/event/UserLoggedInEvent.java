package gov.ca.cwds.event;

import gov.ca.cwds.UniversalUserToken;
import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Created by Alexander Serbin on 9/14/2018
 */
public final class UserLoggedInEvent {

  private final UniversalUserToken userToken;
  private final LocalDateTime loginTime;

  public UserLoggedInEvent(UniversalUserToken userToken) {
    this.userToken = userToken;
    this.loginTime = LocalDateTime.now();
  }

  public String getUserId() {
    return (String) userToken.getParameters().get("userName");
  }

  public LocalDateTime getLoginTime() {
    return loginTime;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
