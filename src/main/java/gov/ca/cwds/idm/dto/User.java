package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import static gov.ca.cwds.rest.api.domain.DomainObject.DATE_FORMAT;
import static gov.ca.cwds.rest.api.domain.DomainObject.TIME_FORMAT;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class User {
  private String id;
  private String firstName;
  private String lastName;
  private String countyName;

  @JsonProperty("RACFID")
  private String racfid;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private Date startDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private Date endDate;

  private String office;
  private String phoneNumber;
  private String phoneExtensionNumber;
  private String email;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private Date userCreateDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private Date userLastModifiedDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT + " " + TIME_FORMAT)
  private LocalDateTime lastLoginDateTime;

  private Boolean enabled;
  private String status;

  private Set<String> permissions = new LinkedHashSet<>();

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

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getOffice() {
    return office;
  }

  public void setOffice(String office) {
    this.office = office;
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

  public String getPhoneExtensionNumber() {
    return phoneExtensionNumber;
  }

  public void setPhoneExtensionNumber(String phoneExtensionNumber) {
    this.phoneExtensionNumber = phoneExtensionNumber;
  }
}
