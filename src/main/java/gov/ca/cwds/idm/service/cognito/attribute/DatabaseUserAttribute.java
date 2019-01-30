package gov.ca.cwds.idm.service.cognito.attribute;

public enum DatabaseUserAttribute implements UserAttribute {

  NOTES("notes");

  private String name;

  DatabaseUserAttribute(String name){
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
