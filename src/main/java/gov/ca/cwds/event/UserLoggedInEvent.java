package gov.ca.cwds.event;

import gov.ca.cwds.UniversalUserToken;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Created by Alexander Serbin on 9/14/2018
 */
public final class UserLoggedInEvent {

  private UniversalUserToken userToken;

  public UserLoggedInEvent(UniversalUserToken userToken) {
    this.userToken = userToken;
  }

  public String getUserId() {
    return (String) userToken.getParameters().get("userName");
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
