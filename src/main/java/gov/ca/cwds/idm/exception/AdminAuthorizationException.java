package gov.ca.cwds.idm.exception;

import gov.ca.cwds.service.messages.MessageCode;

public class AdminAuthorizationException extends IdmException {

  private static final long serialVersionUID = 8938049529562113696L;

  public AdminAuthorizationException(String message, String userMessage, MessageCode errorCode) {
    super(message, userMessage, errorCode);
  }
}
