package gov.ca.cwds.idm.event;

import static gov.ca.cwds.config.api.idm.Roles.replaceRoleIdByName;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRoleChangedEvent extends AdminCausedChangeLogEvent {

  private static final long serialVersionUID = 1145582435628289251L;

  public static final String EVENT_TYPE_USER_ROLE_CHANGED = "Role";

  public UserRoleChangedEvent(User existedUser, StringSetDiff rolesDiff) {
    super(existedUser);
    setEventType(EVENT_TYPE_USER_ROLE_CHANGED);
    setUserRoles(toStringValue(replaceRoleIdByName(rolesDiff.getNewValue())));
    setOldValue(toStringValue(replaceRoleIdByName(rolesDiff.getOldValue())));
    setNewValue(toStringValue(replaceRoleIdByName(rolesDiff.getNewValue())));
  }

  static String toStringValue(Set<String> value) {
    if (value == null) {
      return "";
    } else {
      return StringUtils.join(new TreeSet<>(value), ", ");
    }
  }
}
