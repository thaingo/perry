package gov.ca.cwds.idm.dto;

import static gov.ca.cwds.util.Utils.DATE_FORMAT;
import static gov.ca.cwds.util.Utils.TIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import org.hibernate.validator.constraints.NotBlank;



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@SuppressWarnings("squid:S3437")
public class User implements Serializable {

  private static final long serialVersionUID = -5958790384369574754L;

  private String id;

  @NotBlank private String email;

  @NotBlank private String firstName;

  @NotBlank private String lastName;

  @NotBlank private String countyName;

  private String racfid;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate startDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate endDate;

  private String officeId;

  private String phoneNumber;

  private String phoneExtensionNumber;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private Date userCreateDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private Date userLastModifiedDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT + " " + TIME_FORMAT)
  private LocalDateTime lastLoginDateTime;

  private Boolean enabled;

  private String status;

  private Set<String> permissions = new LinkedHashSet<>();

  private Set<String> roles = new LinkedHashSet<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCountyName() {
    return countyName;
  }

  public void setCountyName(String countyName) {
    this.countyName = countyName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getRacfid() {
    return racfid;
  }

  public void setRacfid(String racfid) {
    this.racfid = racfid;
  }

  public Date getUserCreateDate() {
    return userCreateDate;
  }

  public void setUserCreateDate(Date userCreateDate) {
    this.userCreateDate = userCreateDate;
  }

  public Date getUserLastModifiedDate() {
    return userLastModifiedDate;
  }

  public void setUserLastModifiedDate(Date userLastModifiedDate) {
    this.userLastModifiedDate = userLastModifiedDate;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public String getOfficeId() {
    return officeId;
  }

  public void setOfficeId(String officeId) {
    this.officeId = officeId;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDateTime getLastLoginDateTime() {
    return lastLoginDateTime;
  }

  public void setLastLoginDateTime(LocalDateTime lastLoginDateTime) {
    this.lastLoginDateTime = lastLoginDateTime;
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

  public String getPhoneExtensionNumber() {
    return phoneExtensionNumber;
  }

  public void setPhoneExtensionNumber(String phoneExtensionNumber) {
    this.phoneExtensionNumber = phoneExtensionNumber;
  }
}
