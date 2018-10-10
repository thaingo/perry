package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserByIdResponse implements Serializable {

  private static final long serialVersionUID = 2528332054070849802L;

  private boolean editable;
  private User user;
  private List<String> possibleRoles;

  public boolean isEditable() {
    return editable;
  }

  public User getUser() {
    return user;
  }

  public List<String> getPossibleRoles() {
    return possibleRoles;
  }

  public static final class UserByIdResponseBuilder {

    private boolean editable;
    private User user;
    private List<String> possibleRoles;

    private UserByIdResponseBuilder() {
    }

    public static UserByIdResponseBuilder anUserByIdResponse() {
      return new UserByIdResponseBuilder();
    }

    public UserByIdResponseBuilder withEditable(boolean editable) {
      this.editable = editable;
      return this;
    }

    public UserByIdResponseBuilder withUser(User user) {
      this.user = user;
      return this;
    }

    public UserByIdResponseBuilder withPossibleRoles(List<String> roles) {
      this.possibleRoles = roles;
      return this;
    }

    public UserByIdResponse build() {
      UserByIdResponse userByIdResponse = new UserByIdResponse();
      userByIdResponse.editable = editable;
      userByIdResponse.user = user;
      userByIdResponse.possibleRoles = possibleRoles;
      return userByIdResponse;
    }
  }

}
