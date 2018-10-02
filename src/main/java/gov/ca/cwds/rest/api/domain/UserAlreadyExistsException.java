package gov.ca.cwds.rest.api.domain;

import gov.ca.cwds.service.messages.MessageCode;

public class UserAlreadyExistsException extends IdmException {

  private static final long serialVersionUID = 1812390924134780612L;

  public UserAlreadyExistsException(String message, String userMessage, MessageCode errorCode,
      Throwable e) {
    super(message, userMessage, errorCode, e);
  }

  public UserAlreadyExistsException(String message, MessageCode errorCode, Throwable e) {
    super(message, message, errorCode, e);
  }
}
