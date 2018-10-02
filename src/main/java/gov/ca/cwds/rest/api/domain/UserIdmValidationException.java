package gov.ca.cwds.rest.api.domain;

import gov.ca.cwds.service.messages.MessageCode;

public class UserIdmValidationException extends IdmException {

  private static final long serialVersionUID = -3815312326377563095L;

  public UserIdmValidationException(String message, String userMessage, MessageCode errorCode) {
    super(message, userMessage, errorCode);
  }

  public UserIdmValidationException(
      String message, String userMessage, MessageCode errorCode, Throwable e) {
    super(message, userMessage, errorCode, e);
  }

  public UserIdmValidationException(
      String message, MessageCode errorCode, Throwable e) {
    super(message, message, errorCode, e);
  }
}
