package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IdmNotification implements Serializable {

  private static final long serialVersionUID = -1293310748892133999L;

  private String userId;
  private String actionType;

  @JsonCreator
  public IdmNotification(
      @JsonProperty("userId") String userId,
      @JsonProperty("actionType") String actionType) {
    this.userId = userId;
    this.actionType = actionType;
  }

  public IdmNotification(
      String userId,
      NotificationType notificationType) {
    this.userId = userId;
    this.actionType = notificationType.toString();
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
