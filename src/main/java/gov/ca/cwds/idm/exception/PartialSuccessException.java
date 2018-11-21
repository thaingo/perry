package gov.ca.cwds.idm.exception;

import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.service.messages.MessageCode;
import java.util.Arrays;

public class PartialSuccessException extends IdmException {

  private static final long serialVersionUID = -4103865347957948978L;

  private final String userId;

  private final OperationType operationType;

  public PartialSuccessException(String userId, OperationType operationType,
      String techMessage, String userMessage,
      MessageCode errorCode, Exception... causes) {
    super(techMessage, userMessage, errorCode);
    this.setCauses(Arrays.asList(causes));
    this.userId = userId;
    this.operationType = operationType;
  }

  public PartialSuccessException(String userId, OperationType operationType,
      String techMessage, MessageCode errorCode, Exception... causes) {
    this(userId, operationType, techMessage, techMessage, errorCode, causes);
  }

  public String getUserId() {
    return userId;
  }

  public OperationType getOperationType() {
    return operationType;
  }
}
