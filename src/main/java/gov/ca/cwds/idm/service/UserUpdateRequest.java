package gov.ca.cwds.idm.service;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import java.util.Map;

/**
 * Created by Alexander Serbin on 1/20/2019
 */
public class UserUpdateRequest {

  private UserType existedUser;
  private String userId;
  private Map<UserAttribute, UserAttributeDiff> diffMap;
  private User user;

  public void setExistedUser(UserType existedUser) {
    this.existedUser = existedUser;
  }

  public UserType getExistedUser() {
    return existedUser;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void setDiffMap(Map<UserAttribute, UserAttributeDiff> diffMap) {
    this.diffMap = diffMap;
  }

  public Map<UserAttribute, UserAttributeDiff> getDiffMap() {
    return diffMap;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public boolean isAttributeChanged(UserAttribute userAttribute) {
    return diffMap.containsKey(userAttribute);
  }

  public String getOldValueAsString(UserAttribute userAttribute) {
    return diffMap.get(userAttribute).getOldValueAsString();
  }

  public String getNewValueAsString(UserAttribute userAttribute) {
    return diffMap.get(userAttribute).getNewValueAsString();
  }

}
