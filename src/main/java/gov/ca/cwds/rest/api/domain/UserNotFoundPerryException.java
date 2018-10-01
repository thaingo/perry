package gov.ca.cwds.rest.api.domain;

import gov.ca.cwds.service.messages.MessageCode;

public class UserNotFoundPerryException extends IdmException {

  private static final long serialVersionUID = -2786549062239961277L;

  public UserNotFoundPerryException(String message, String userMessage, MessageCode errorCode, Throwable e) {
    super(message, userMessage, errorCode, e);
  }

  public UserNotFoundPerryException(String message, MessageCode errorCode, Throwable e) {
    super(message, message, errorCode, e);
  }
}
