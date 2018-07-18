package gov.ca.cwds.idm.service.cognito;

public enum CustomUserAttribute {
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

  public String getName() {
    return name;
  }
}
