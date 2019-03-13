package gov.ca.cwds.idm.service.cognito.attribute;

public enum CustomUserAttribute implements UserAttribute {
  OFFICE("custom:Office"),
  COUNTY("custom:County"),
  IS_LOCKED("custom:accountLocked"),
  MAX_LOGIN_ATTEMPTS("custom:numLoginAttempts");

  private String name;

  CustomUserAttribute(String name){
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
