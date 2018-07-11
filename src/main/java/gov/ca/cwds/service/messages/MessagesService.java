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

  public String get(String code) {
    return accessor.getMessage(code);
  }

  public String get(String code, Object... args) {
    return accessor.getMessage(code, args);
  }
}
