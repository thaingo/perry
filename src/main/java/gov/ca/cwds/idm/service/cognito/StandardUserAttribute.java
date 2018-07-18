package gov.ca.cwds.idm.service.cognito;

public enum StandardUserAttribute {
  EMAIL("email"),
  FIRST_NAME("given_name"),
  LAST_NAME("family_name"),
  PHONE_NUMBER("phone_number"),
  RACFID_STANDARD("preferred_username");

  private String name;

  StandardUserAttribute(String name){
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
