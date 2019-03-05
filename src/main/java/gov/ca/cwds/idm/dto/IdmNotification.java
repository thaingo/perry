package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IdmNotification implements Serializable {

  private static final long serialVersionUID = 2295438672867451984L;

  private String userId;
  private String actionType;

  public IdmNotification() {
  }

  public IdmNotification(String userId, String actionType) {
    this.userId = userId;
    this.actionType = actionType;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }
}
