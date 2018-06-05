package gov.ca.cwds.security.realm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Set;

/** Created by dmitry.rudenko on 6/2/2017. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerryAccount {
  @JsonProperty private String user;

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  @JsonProperty private Set<String> roles;
  @JsonProperty private String staffId;

  @JsonProperty("county_code")
  private String countyCode;

  @JsonProperty("county_cws_code")
  private String countyCwsCode;

  @JsonProperty("county_name")
  private String countyName;

  @JsonProperty private Set<String> privileges;
  @JsonProperty private Set<String> authorityCodes;

  public PerryAccount(String user) {
    this.user = user;
  }

  /** Default Constructor */
  public PerryAccount() {}

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public Set<String> getRoles() {
    return roles;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }

  public String getStaffId() {
    return staffId;
  }

  public void setStaffId(String staffId) {
    this.staffId = staffId;
  }

  public String getCountyCode() {
    return countyCode;
  }

  public void setCountyCode(String countyCode) {
    this.countyCode = countyCode;
  }

  public String getCountyCwsCode() {
    return countyCwsCode;
  }

  public void setCountyCwsCode(String countyCwsCode) {
    this.countyCwsCode = countyCwsCode;
  }

  public String getCountyName() {
    return countyName;
  }

  public void setCountyName(String countyName) {
    this.countyName = countyName;
  }

  public Set<String> getPrivileges() {
    return privileges;
  }

  public void setPrivileges(Set<String> privileges) {
    this.privileges = privileges;
  }

  public Set<String> getAuthorityCodes() {
    return authorityCodes;
  }

  public void setAuthorityCodes(Set<String> authorityCodes) {
    this.authorityCodes = authorityCodes;
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

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, true);
  }
}
