package gov.ca.cwds.idm.service.cognito.attribute;

public enum StandardUserAttribute implements UserAttribute {
  EMAIL("email"),
  EMAIL_VERIFIED("email_verified"),
  FIRST_NAME("given_name"),
  LAST_NAME("family_name"),
  RACFID_STANDARD("preferred_username"),
  PHONE_NUMBER("phone_number");

  private String name;

  StandardUserAttribute(String name){
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
