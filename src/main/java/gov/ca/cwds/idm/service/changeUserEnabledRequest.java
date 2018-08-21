package gov.ca.cwds.idm.service;

public class ChangeUserEnabledRequest {

  private final String userId;
  private final Boolean existedEnabled;
  private final Boolean newEnabled;

  public ChangeUserEnabledRequest(String userId, Boolean existedEnabled, Boolean newEnabled) {
    this.userId = userId;
    this.existedEnabled = existedEnabled;
    this.newEnabled = newEnabled;
  }

  public String getUserId() {
    return userId;
  }

  public Boolean getExistedEnabled() {
    return existedEnabled;
  }

  public Boolean getNewEnabled() {
    return newEnabled;
  }
}