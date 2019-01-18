package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import java.util.Map;
import org.apache.commons.lang3.Validate;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRoleChangedEvent extends UserAttributeChangedEvent {

  private static final long serialVersionUID = 1145582435628289251L;

  public static final String EVENT_TYPE_USER_ROLE_CHANGED = "User Role Changed";

  public UserRoleChangedEvent(User user, Map<UserAttribute, UserAttributeDiff> diffMap) {
    super(user, diffMap);
    Validate.isTrue(diffMap.containsKey(ROLES));
    setEventType(EVENT_TYPE_USER_ROLE_CHANGED);
    setOldValue(diffMap.get(ROLES).getOldValueAsString());
    setNewValue(diffMap.get(ROLES).getNewValueAsString());
  }

}
