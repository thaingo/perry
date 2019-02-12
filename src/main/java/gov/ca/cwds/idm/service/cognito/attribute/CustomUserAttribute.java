package gov.ca.cwds.idm.service.cognito.attribute;

public enum CustomUserAttribute implements UserAttribute {
  PERMISSIONS("custom:Permission"),
  ROLES("custom:Role"),
  OFFICE("custom:Office"),
  COUNTY("custom:County"),
  RACFID_CUSTOM("custom:RACFID"),
  RACFID_CUSTOM_2("custom:RACFId"),
  PHONE_EXTENSION("custom:PhoneExtension"),
  FAILED_LOGINS_COUNT("custom:lockout_count");

  private String name;

  CustomUserAttribute(String name){
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
