package gov.ca.cwds.service.messages;

public class Messages {

  private final MessageCode messageCode;
  private final String techMsg;
  private final String userMsg;

  public Messages(MessageCode messageCode, String techMsg, String userMsg) {
    this.messageCode = messageCode;
    this.techMsg = techMsg;
    this.userMsg = userMsg;
  }

  public MessageCode getMessageCode() {
    return messageCode;
  }

  public String getTechMsg() {
    return techMsg;
  }

  public String getUserMsg() {
    return userMsg;
  }
}