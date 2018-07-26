package gov.ca.cwds.idm.service.cognito;

public enum CustomUserAttribute implements UserAttribute {
  PERMISSIONS("custom:Permission"),
  ROLES("custom:Role"),
  OFFICE("custom:Office"),
  COUNTY("custom:County"),
  RACFID_CUSTOM("custom:RACFID"),
  RACFID_CUSTOM_2("custom:RACFId");

  private String name;

  CustomUserAttribute(String name){
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
