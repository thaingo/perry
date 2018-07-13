package gov.ca.cwds.idm.dto;

import com.amazonaws.services.cognitoidp.model.UserType;

import java.util.List;

public class CognitoUserPage {
  private List<UserType> users;
  private String paginationToken;

  public CognitoUserPage(List<UserType> users, String paginationToken) {
    this.users = users;
    this.paginationToken = paginationToken;
  }

  public List<UserType> getUsers() {
    return users;
  }

  public void setUsers(List<UserType> users) {
    this.users = users;
  }

  public String getPaginationToken() {
    return paginationToken;
  }

  public void setPaginationToken(String paginationToken) {
    this.paginationToken = paginationToken;
  }
}
