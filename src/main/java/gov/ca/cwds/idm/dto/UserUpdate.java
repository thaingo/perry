package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserUpdate implements Serializable {

  private static final long serialVersionUID = -327995503281234136L;

  private String email;
  private Boolean enabled;
  private String phoneNumber;
  private String phoneExtensionNumber;
  private Set<String> permissions;
  private Set<String> roles;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPhoneExtensionNumber() {
    return phoneExtensionNumber;
  }

  public void setPhoneExtensionNumber(String phoneExtensionNumber) {
    this.phoneExtensionNumber = phoneExtensionNumber;
  }

  public Set<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(Set<String> permissions) {
    this.permissions = permissions;
  }

  public Set<String> getRoles() {
    return roles;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }
}
