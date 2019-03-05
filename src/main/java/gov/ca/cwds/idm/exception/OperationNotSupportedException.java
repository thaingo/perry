package gov.ca.cwds.idm.exception;

import gov.ca.cwds.service.messages.MessageCode;

public class OperationNotSupportedException extends IdmException {


  private static final long serialVersionUID = 6662020729194827707L;

  public OperationNotSupportedException(String message, String userMessage, MessageCode errorCode) {
    super(message, userMessage, errorCode);
  }

  public OperationNotSupportedException(
      String message, String userMessage, MessageCode errorCode, Throwable e) {
    super(message, userMessage, errorCode, e);
  }

  public OperationNotSupportedException(
      String message, MessageCode errorCode, Throwable e) {
    super(message, message, errorCode, e);
  }
}
