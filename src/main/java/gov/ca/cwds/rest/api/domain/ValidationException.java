package gov.ca.cwds.rest.api.domain;

import gov.ca.cwds.service.messages.MessageCode;

public class ValidationException extends RuntimeException {

  private static final long serialVersionUID = 3122648071078983803L;
  private final MessageCode errorCode;

  public ValidationException(MessageCode errorCode, String message, Throwable e) {
    super(message, e);
    this.errorCode = errorCode;
  }

  public ValidationException(MessageCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public MessageCode getErrorCode() {
    return errorCode;
  }
}
