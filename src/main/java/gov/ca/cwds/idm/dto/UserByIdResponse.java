package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserByIdResponse implements Serializable {

  private static final long serialVersionUID = -7644760098359425249L;

  private User user;
  private UserEditDetails editDetails;

  public UserByIdResponse(User user, UserEditDetails editDetails) {
    this.user = user;
    this.editDetails = editDetails;
  }

  public User getUser() {
    return user;
  }

  public UserEditDetails getEditDetails() {
    return editDetails;
  }
}
