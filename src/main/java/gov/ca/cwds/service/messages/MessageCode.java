package gov.ca.cwds.service.messages;

public enum MessageCode {
  ERROR_CONNECT_TO_IDM("CAP-001"),
  USER_WITH_EMAIL_EXISTS_IN_IDM("CAP-002"),
  NO_USER_WITH_RACFID_IN_CWSCMS("CAP-003"),
  NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY("CAP-004"),
  IDM_NEW_USER_VALIDATION_FAILED("CAP-005"),
  IDM_MAPPING_SCRIPT_ERROR("CAP-006"),
  DUPLICATE_USERID_FOR_RACFID_IN_CWSCMS("CAP-007"),
  USER_NOT_FOUND_BY_ID_IN_IDM("CAP-008"),
  ERROR_GET_USER_FROM_IDM("CAP-009"),
  ERROR_UPDATE_USER_IN_IDM("CAP-010"),
  UNABLE_CREATE_NEW_IDM_USER("CAP-011");

  private String value;

  MessageCode(String value){
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
