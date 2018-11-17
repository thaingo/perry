package gov.ca.cwds.idm.exception;

import gov.ca.cwds.service.messages.MessageCode;
import java.util.ArrayList;
import java.util.List;

public class IdmException extends RuntimeException {

  private static final long serialVersionUID = -6897429965862778311L;

  private final String userMessage;

  private final MessageCode errorCode;

  private final List<Exception> causes = new ArrayList<>();

  public IdmException(String techMessage, String userMessage, MessageCode errorCode) {
    super(techMessage);
    this.userMessage = userMessage;
    this.errorCode = errorCode;
  }

  public IdmException(String techMessage, String userMessage, MessageCode errorCode, Throwable e) {
    super(techMessage + ": " + e.getMessage(), e);
    this.userMessage = userMessage;
    this.errorCode = errorCode;
  }

  public MessageCode getErrorCode() {
    return errorCode;
  }

  public String getUserMessage() {
    return userMessage;
  }

  public void setCauses(List<Exception> causes) {
    this.causes.clear();
    if(causes != null) {
      this.causes.addAll(causes);
    }
  }

  public List<Exception> getCauses() {
    return causes;
  }
}
