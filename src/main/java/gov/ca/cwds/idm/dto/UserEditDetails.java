package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserEditDetails implements Serializable {

  private static final long serialVersionUID = -3136968159403822894L;

  private boolean editable;

  @JsonProperty("roles")
  private ListOfValues roles;

  @JsonProperty("permissions")
  private ListOfValues permissions;

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public ListOfValues getRoles() {
    return roles;
  }

  public void setRoles(ListOfValues roles) {
    this.roles = roles;
  }

  public ListOfValues getPermissions() {
    return permissions;
  }

  public void setPermissions(ListOfValues permissions) {
    this.permissions = permissions;
  }
}
