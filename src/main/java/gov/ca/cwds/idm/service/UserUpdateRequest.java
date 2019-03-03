package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.UpdateDifference;


public class UserUpdateRequest {

  private String userId;
  private User existedUser;
  private UpdateDifference updateDifference;

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void setExistedUser(User existedUser) {
    this.existedUser = existedUser;
  }

  public User getExistedUser() {
    return existedUser;
  }

  public UpdateDifference getUpdateDifference() {
    return updateDifference;
  }

  public void setUpdateDifference(UpdateDifference updateDifference) {
    this.updateDifference = updateDifference;
  }

}
