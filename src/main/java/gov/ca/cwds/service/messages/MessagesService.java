package gov.ca.cwds.service.messages;

import static gov.ca.cwds.config.Constants.DEFAULT_LOCALE;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

@Service
public class MessagesService {

  @Autowired
  @Qualifier("tech")
  private MessageSource techMessageSource;

  @Autowired
  @Qualifier("user")
  private MessageSource userMessageSource;

  private MessageSourceAccessor techMessagesAccessor;

  private MessageSourceAccessor userMessagesAccessor;

  @PostConstruct
  private void init() {
    techMessagesAccessor = new MessageSourceAccessor(techMessageSource, DEFAULT_LOCALE);
    userMessagesAccessor = new MessageSourceAccessor(userMessageSource, DEFAULT_LOCALE);
  }

  public String getTechMessage(MessageCode messageCode, String... args) {
    return techMessagesAccessor.getMessage(messageCode.getValue(), args);
  }

  public String getUserMessage(MessageCode messageCode, String... args) {
    return userMessagesAccessor.getMessage(messageCode.getValue(), args);
  }

  public Messages getMessages(MessageCode messageCode, String... args) {
    String techMsg = getTechMessage(messageCode, args);
    String userMsg = getUserMessage(messageCode, args);
    return new Messages(techMsg, userMsg);
  }

  public static class Messages {

    private final String techMsg;
    private final String userMsg;

    public Messages(String techMsg, String userMsg) {
      this.techMsg = techMsg;
      this.userMsg = userMsg;
    }

    public String getTechMsg() {
      return techMsg;
    }

    public String getUserMsg() {
      return userMsg;
    }
  }
}
