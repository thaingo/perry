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
  private MessageSource messageSource;

  private MessageSourceAccessor accessor;

  @PostConstruct
  private void init() {
    accessor = new MessageSourceAccessor(messageSource, DEFAULT_LOCALE);
  }

  public String getTech(MessageCode messageCode, Object... args) {
    return accessor.getMessage(messageCode.getValue(), args);
  }

  public void setMessageSource(MessageSource messageSource) {
    this.messageSource = messageSource;
  }
}
