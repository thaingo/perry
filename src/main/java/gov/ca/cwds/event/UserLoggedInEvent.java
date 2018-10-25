package gov.ca.cwds.event;

import static gov.ca.cwds.util.UniversalUserTokenDeserializer.USER_NAME;

import gov.ca.cwds.UniversalUserToken;
import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Created by Alexander Serbin on 9/14/2018
 */
public final class UserLoggedInEvent {

  private final UniversalUserToken userToken;
  private final LocalDateTime loginTime;

  public UserLoggedInEvent(UniversalUserToken userToken, LocalDateTime loginTime) {
    this.userToken = userToken;
    this.loginTime = loginTime;
  }

  public String getUserId() {
    return (String) userToken.getParameters().get(USER_NAME);
  }

  public LocalDateTime getLoginTime() {
    return loginTime;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
