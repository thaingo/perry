package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;

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
public class EmailChangedEvent extends UserAttributeChangedEvent {

  private static final long serialVersionUID = 5740708683905011516L;

  public static final String EVENT_TYPE_EMAIL_CHANGED = "User's Email Address Changed";

  public EmailChangedEvent(User user, Map<UserAttribute, UserAttributeDiff> diffMap) {
    super(user, diffMap);
    Validate.isTrue(diffMap.containsKey(EMAIL));
    setEventType(EVENT_TYPE_EMAIL_CHANGED);
    setOldValue(diffMap.get(EMAIL).getOldValueAsString());
    setNewValue(diffMap.get(EMAIL).getNewValueAsString());
  }

}
