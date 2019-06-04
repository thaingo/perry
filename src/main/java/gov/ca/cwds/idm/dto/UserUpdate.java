package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserUpdate implements Serializable {

  private static final long serialVersionUID = -5847105938540972160L;

  private String email;
  private boolean emailUpdateRequested;

  private Boolean enabled;
  private boolean enabledUpdateRequested;

  private String phoneNumber;
  private boolean phoneNumberUpdateRequested;

  private String phoneExtensionNumber;
  private boolean phoneExtensionNumberUpdateRequested;

  private String cellPhoneNumber;
  private boolean cellPhoneNumberUpdateRequested;

  private String notes;
  private boolean notesUpdateRequested;

  private Set<String> permissions;
  private boolean permissionsUpdateRequested;

  private Set<String> roles;
  private boolean rolesUpdateRequested;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
    emailUpdateRequested = true;
  }

  public boolean isEmailUpdateRequested() {
    return emailUpdateRequested;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
    enabledUpdateRequested = true;
  }

  public boolean isEnabledUpdateRequested() {
    return enabledUpdateRequested;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    phoneNumberUpdateRequested = true;
  }

  public boolean isPhoneNumberUpdateRequested() {
    return phoneNumberUpdateRequested;
  }

  public String getPhoneExtensionNumber() {
    return phoneExtensionNumber;
  }

  public void setPhoneExtensionNumber(String phoneExtensionNumber) {
    this.phoneExtensionNumber = phoneExtensionNumber;
    phoneExtensionNumberUpdateRequested = true;
  }

  public boolean isPhoneExtensionNumberUpdateRequested() {
    return phoneExtensionNumberUpdateRequested;
  }

  public String getCellPhoneNumber() {
    return cellPhoneNumber;
  }

  public void setCellPhoneNumber(String cellPhoneNumber) {
    this.cellPhoneNumber = cellPhoneNumber;
    cellPhoneNumberUpdateRequested = true;
  }

  public boolean isCellPhoneNumberUpdateRequested() {
    return cellPhoneNumberUpdateRequested;
  }

  public Set<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(Set<String> permissions) {
    this.permissions = permissions;
    permissionsUpdateRequested = true;
  }

  public boolean isPermissionsUpdateRequested() {
    return permissionsUpdateRequested;
  }

  public Set<String> getRoles() {
    return roles;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
    rolesUpdateRequested = true;
  }

  public boolean isRolesUpdateRequested() {
    return rolesUpdateRequested;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
    notesUpdateRequested = true;
  }

  public boolean isNotesUpdateRequested() {
    return notesUpdateRequested;
  }
}
