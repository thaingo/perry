package gov.ca.cwds.rest.api.domain;

import gov.ca.cwds.service.messages.MessageCode;

public abstract class IdmException extends RuntimeException {

  private static final long serialVersionUID = -6897429965862778311L;

  private final String userMessage;

  private final MessageCode errorCode;

  public IdmException(String techMessage, String userMessage, MessageCode errorCode) {
    super(techMessage);
    this.userMessage = userMessage;
    this.errorCode = errorCode;
  }

  public IdmException(String techMessage, String userMessage, MessageCode errorCode, Throwable e) {
    super(techMessage, e);
    this.userMessage = userMessage;
    this.errorCode = errorCode;
  }

  public MessageCode getErrorCode() {
    return errorCode;
  }

  public String getUserMessage() {
    return userMessage;
  }
}
