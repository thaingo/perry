package gov.ca.cwds.idm.dto;

import java.util.List;

public class User {
  private String username;
  private String countyName;
  List<String> attributes;

  public User(String username, String countyName, List<String> attributes) {
    this.username = username;
    this.countyName = countyName;
    this.attributes = attributes;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getCountyName() {
    return countyName;
  }

  public void setCountyName(String countyName) {
    this.countyName = countyName;
  }

  public List<String> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<String> attributes) {
    this.attributes = attributes;
  }
}
