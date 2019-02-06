package gov.ca.cwds.idm.event;

import static gov.ca.cwds.config.api.idm.Roles.joinRoles;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRegistrationResentEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 968999099511509164L;

  public static final String EVENT_TYPE_REGISTRATION_RESENT = "Registration Resent";

  public UserRegistrationResentEvent(User user) {
    super(EVENT_TYPE_REGISTRATION_RESENT, user);
  }

}
