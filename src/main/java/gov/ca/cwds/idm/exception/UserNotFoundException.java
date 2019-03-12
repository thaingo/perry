package gov.ca.cwds.idm.exception;

import gov.ca.cwds.service.messages.MessageCode;

public class UserNotFoundException extends IdmException {

  private static final long serialVersionUID = -2786549062239961277L;

  public UserNotFoundException(String message, String userMessage, MessageCode errorCode,
      Throwable e) {
    super(message, userMessage, errorCode, e);
  }

  public UserNotFoundException(String message, String userMessage, MessageCode errorCode) {
    super(message, userMessage, errorCode);
  }
}
