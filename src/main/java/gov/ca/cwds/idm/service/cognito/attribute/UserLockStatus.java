package gov.ca.cwds.idm.service.cognito.attribute;

public enum UserLockStatus {
  TRUE("1"),
  FALSE("0");

  private final String value;

  UserLockStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
