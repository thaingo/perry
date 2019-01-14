package gov.ca.cwds.idm.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserChangeLogRecord implements Serializable {
  private static final long serialVersionUID = 8393081330310394261L;


  private String adminRole;
  private String adminName;
  private String userRoles;
  private String userId;
  private String userName;
  private String oldValue;
  private String newValue;
  private String officeId;
  private String countyName;


  public String getAdminRole() {
    return adminRole;
  }

  public void setAdminRole(String adminRole) {
    this.adminRole = adminRole;
  }

  public String getAdminName() {
    return adminName;
  }

  public void setAdminName(String adminName) {
    this.adminName = adminName;
  }

  public String getUserRoles() {
    return userRoles;
  }

  public void setUserRoles(String userRoles) {
    this.userRoles = userRoles;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getOfficeId() {
    return officeId;
  }

  public void setOfficeId(String officeId) {
    this.officeId = officeId;
  }

  public String getCountyName() {
    return countyName;
  }

  public void setCountyName(String countyName) {
    this.countyName = countyName;
  }


  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }
}
