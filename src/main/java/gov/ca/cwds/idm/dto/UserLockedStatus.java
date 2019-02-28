package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserLockedStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(example = "false")
  private boolean locked;

  public UserLockedStatus(boolean locked) {
    this.locked = locked;
  }

  public UserLockedStatus() {}

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }
}
