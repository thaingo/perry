package gov.ca.cwds.service.messages;

import java.util.Locale;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

@Service
public class MessagesService {

  private static final Locale DEFAULT_LOCALE  = Locale.US;

  @Autowired
  private MessageSource messageSource;

  private MessageSourceAccessor accessor;

  @PostConstruct
  private void init() {
    accessor = new MessageSourceAccessor(messageSource, DEFAULT_LOCALE);
  }

  public String get(MessageCode messageCode, Object... args) {
    return accessor.getMessage(messageCode.getValue(), args);
  }

  public void setMessageSource(MessageSource messageSource) {
    this.messageSource = messageSource;
  }
}
