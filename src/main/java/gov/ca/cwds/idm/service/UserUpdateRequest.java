package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.Diff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import java.util.Map;

/**
 * Created by Alexander Serbin on 1/20/2019
 */
public class UserUpdateRequest {

  private String userId;
  private User existedUser;
  private Map<UserAttribute, UserAttributeDiff> cognitoDiffMap;

  private Map<UserAttribute, Diff> databaseDiffMap;

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void setCognitoDiffMap(Map<UserAttribute, UserAttributeDiff> cognitoDiffMap) {
    this.cognitoDiffMap = cognitoDiffMap;
  }

  public Map<UserAttribute, UserAttributeDiff> getCognitoDiffMap() {
    return cognitoDiffMap;
  }

  public void setExistedUser(User existedUser) {
    this.existedUser = existedUser;
  }

  public User getExistedUser() {
    return existedUser;
  }

  public boolean isAttributeChanged(UserAttribute userAttribute) {
    return cognitoDiffMap.containsKey(userAttribute);
  }

  public String getOldValueAsString(UserAttribute userAttribute) {
    return cognitoDiffMap.get(userAttribute).getOldValueAsString();
  }

  public String getNewValueAsString(UserAttribute userAttribute) {
    return cognitoDiffMap.get(userAttribute).getNewValueAsString();
  }

  public Map<UserAttribute, Diff> getDatabaseDiffMap() {
    return databaseDiffMap;
  }

  public void setDatabaseDiffMap(
      Map<UserAttribute, Diff> databaseDiffMap) {
    this.databaseDiffMap = databaseDiffMap;
  }
}
