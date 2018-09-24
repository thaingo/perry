package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserByIdResponse implements Serializable {

  private static final long serialVersionUID = 786532350090095256L;
  private boolean editable;
  private User user;

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }


  public static final class UserByIdResponseBuilder {

    private boolean editable;
    private User user;

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

    public UserByIdResponse build() {
      UserByIdResponse userByIdResponse = new UserByIdResponse();
      userByIdResponse.setEditable(editable);
      userByIdResponse.setUser(user);
      return userByIdResponse;
    }
  }
}
