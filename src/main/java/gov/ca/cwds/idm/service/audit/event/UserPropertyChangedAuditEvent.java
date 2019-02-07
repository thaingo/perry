package gov.ca.cwds.idm.service.audit.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.StringDiff;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserPropertyChangedAuditEvent extends UserAuditEvent {

  private static final long serialVersionUID = 2080736700082901748L;

  public UserPropertyChangedAuditEvent(String eventType, User existedUser, StringDiff strPropertyDiff) {
    super(eventType, existedUser);
    setOldValue(strPropertyDiff.getOldValue());
    setNewValue(strPropertyDiff.getNewValue());
  }
}
