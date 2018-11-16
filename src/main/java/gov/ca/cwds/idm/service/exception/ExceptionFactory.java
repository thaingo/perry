package gov.ca.cwds.idm.service.exception;

import gov.ca.cwds.idm.exception.IdmException;
import gov.ca.cwds.idm.exception.UserValidationException;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class ExceptionFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionFactory.class);

  private MessagesService messagesService;

  @Autowired
  public void setMessagesService(MessagesService messagesService) {
    this.messagesService = messagesService;
  }

  public IdmException createIdmException(MessageCode messageCode, Object... args) {
    return createException(IdmException::new, messageCode, args);
  }

  public UserValidationException createValidationException(MessageCode messageCode,
      Object... args) {
    return createException(UserValidationException::new, messageCode, args);
  }

  private <T extends IdmException> T createException(IdmExceptionCreator<T> creator, MessageCode messageCode, Object... args) {
    String techMsg = messagesService.getTechMessage(messageCode, args);
    String userMsg = messagesService.getUserMessage(messageCode, args);
    LOGGER.error(techMsg);
    return creator.create(techMsg, userMsg, messageCode);
  }

  interface IdmExceptionCreator<T extends IdmException> {
    T create(String techMsg, String userMsg, MessageCode messageCode);
  }
}
