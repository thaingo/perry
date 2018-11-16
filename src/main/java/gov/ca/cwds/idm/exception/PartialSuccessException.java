package gov.ca.cwds.idm.exception;

import gov.ca.cwds.service.messages.MessageCode;
import java.util.Arrays;

public class PartialSuccessException extends IdmException {

  private static final long serialVersionUID = -4396802496455959898L;

  private String userId;


  public PartialSuccessException(String userId, String techMessage, String userMessage,
      MessageCode errorCode, Exception... causes) {
    super(techMessage, userMessage, errorCode);
    this.setCauses(Arrays.asList(causes));
    this.userId = userId;
  }

  public PartialSuccessException(String userId, String techMessage, MessageCode errorCode,
      Exception... causes) {
    this(userId, techMessage, techMessage, errorCode, causes);
  }

  public String getUserId() {
    return userId;
  }

}
