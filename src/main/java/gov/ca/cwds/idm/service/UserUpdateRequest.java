package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.diff.Differencing;

/**
 * Created by Alexander Serbin on 1/20/2019
 */
public class UserUpdateRequest {

  private String userId;
  private User existedUser;
  private Differencing differencing;

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

  public Differencing getDifferencing() {
    return differencing;
  }

  public void setDifferencing(Differencing differencing) {
    this.differencing = differencing;
  }
}
