package gov.ca.cwds.service.messages;

public enum MessageCodes {
  IDM_MAPPING_SCRIPT_ERROR("IDM_MAPPING_SCRIPT_ERROR"),
  DUPLICATE_USERID_FOR_RACFID("DUPLICATE_USERID_FOR_RACFID"),
  NO_USER_WITH_RACFID("NO_USER_WITH_RACFID"),
  USER_WITH_EMAIL_ALREADY_EXISTS("USER_WITH_EMAIL_ALREADY_EXISTS"),
  NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY("NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY");

  private String value;

  MessageCodes(String value){
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
