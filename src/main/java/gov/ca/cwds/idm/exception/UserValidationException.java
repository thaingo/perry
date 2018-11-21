package gov.ca.cwds.idm.exception;

import gov.ca.cwds.service.messages.MessageCode;

public class UserValidationException extends IdmException {

  private static final long serialVersionUID = -3815312326377563095L;

  public UserValidationException(String message, String userMessage, MessageCode errorCode) {
    super(message, userMessage, errorCode);
  }

  public UserValidationException(
      String message, String userMessage, MessageCode errorCode, Throwable e) {
    super(message, userMessage, errorCode, e);
  }

  public UserValidationException(
      String message, MessageCode errorCode, Throwable e) {
    super(message, message, errorCode, e);
  }
}
