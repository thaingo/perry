package gov.ca.cwds.idm.service;

import static java.util.Collections.emptyMap;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.diff.Diff;
import gov.ca.cwds.idm.service.diff.Differencing;
import gov.ca.cwds.idm.service.diff.UserAttributeDiff;
import java.util.Map;

/**
 * Created by Alexander Serbin on 1/20/2019
 */
public class UserUpdateRequest {

  private String userId;
  private User existedUser;
  private Map<UserAttribute, UserAttributeDiff> cognitoDiffMap = emptyMap();
  private Differencing differencing;

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
    return getDiff(userAttribute).getOldValueAsString();
  }

  public String getNewValueAsString(UserAttribute userAttribute) {
    return getDiff(userAttribute).getNewValueAsString();
  }

  private Diff getDiff(UserAttribute userAttribute) {
      return cognitoDiffMap.get(userAttribute);
  }

  public Differencing getDifferencing() {
    return differencing;
  }

  public void setDifferencing(Differencing differencing) {
    this.differencing = differencing;
  }
}
