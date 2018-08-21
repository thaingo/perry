package gov.ca.cwds.service.messages;

public enum MessageCode {
  ERROR_CONNECT_TO_IDM("CAP-001"),
  USER_WITH_EMAIL_EXISTS_IN_IDM("CAP-002"),
  NO_USER_WITH_RACFID_IN_CWSCMS("CAP-003"),
  NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY("CAP-004"),
  IDM_USER_VALIDATION_FAILED("CAP-005"),
  IDM_MAPPING_SCRIPT_ERROR("CAP-006"),
  DUPLICATE_USERID_FOR_RACFID_IN_CWSCMS("CAP-007"),
  USER_NOT_FOUND_BY_ID_IN_IDM("CAP-008"),
  ERROR_GET_USER_FROM_IDM("CAP-009"),
  ERROR_UPDATE_USER_IN_IDM("CAP-010"),
  UNABLE_CREATE_NEW_IDM_USER("CAP-011"),
  UNABLE_LOG_IDM_USER_CREATE("CAP-012"),
  UNABLE_LOG_IDM_USER_UPDATE("CAP-013"),
  UNABLE_LOG_IDM_USER("CAP-014"),
  UNABLE_CREATE_IDM_USER_IN_ES("CAP-015"),
  UNABLE_UPDATE_IDM_USER_IN_ES("CAP-016"),
  INVALID_DATE_FORMAT("CAP-017"),
  USER_CREATE_SAVE_TO_SEARCH_ERROR("CAP-018"),
  USER_CREATE_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS("CAP-019"),
  USER_UPDATE_SAVE_TO_SEARCH_ERROR("CAP-020"),
  USER_UPDATE_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS("CAP-021"),
  USER_ENABLE_UPDATE_ERROR("CAP-022"),
  USER_ENABLE_UPDATE_AND_SAVE_TO_SEARCH_ERRORS("CAP-023"),
  USER_ENABLE_UPDATE_AND_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS("CAP-024"),
  ERROR_UPDATE_USER_ENABLED_STATUS("CAP-025");

  private String value;

  MessageCode(String value){
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
