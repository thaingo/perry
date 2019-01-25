package gov.ca.cwds.idm.service.cognito.attribute;

/**
 * Created by Alexander Serbin on 1/21/2019
 */
public enum OtherUserAttribute implements UserAttribute {

  ENABLED_STATUS("account_status");

  private String name;

  OtherUserAttribute(String name){
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
