package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.StringDiff;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PermissionsChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 5084376904261765730L;

  public static final String EVENT_TYPE_PERMISSIONS_CHANGED = "Permission Change";

  public PermissionsChangedEvent(User existedUser, StringDiff permissionsDiff) {
    super(EVENT_TYPE_PERMISSIONS_CHANGED, existedUser);

    setOldValue(permissionsDiff.getOldValue());
    setNewValue(permissionsDiff.getNewValue());
  }
}
