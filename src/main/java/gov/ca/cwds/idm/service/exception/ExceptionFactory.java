package gov.ca.cwds.idm.service.exception;

import gov.ca.cwds.idm.exception.IdmException;
import gov.ca.cwds.idm.exception.UserAlreadyExistsException;
import gov.ca.cwds.idm.exception.UserNotFoundException;
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

  interface IdmExceptionCreator<T extends IdmException> {
    T create(String techMsg, String userMsg, MessageCode messageCode);
  }

  interface IdmExceptionWithCauseCreator<T extends IdmException> {
    T create(String techMsg, String userMsg, MessageCode messageCode, Throwable cause);
  }

  public IdmException createIdmException(MessageCode messageCode, Throwable cause, Object... args) {
    return createException(IdmException::new, messageCode, cause, args);
  }

  public UserNotFoundException createUserNotFoundException(MessageCode messageCode, Throwable cause, Object... args) {
    return createException(UserNotFoundException::new, messageCode, cause, args);
  }

  public UserAlreadyExistsException createUserAlreadyExistsException(MessageCode messageCode, Throwable cause, Object... args) {
    return createException(UserAlreadyExistsException::new, messageCode, cause, args);
  }

  public UserValidationException createValidationException(MessageCode messageCode,
      Object... args) {
    return createException(UserValidationException::new, messageCode, args);
  }

  public UserValidationException createValidationException(MessageCode messageCode, Throwable cause,
      Object... args) {
    return createException(UserValidationException::new, messageCode, cause, args);
  }

  private <T extends IdmException> T createException(IdmExceptionCreator<T> creator,
      MessageCode messageCode, Object... args) {
    String techMsg = messagesService.getTechMessage(messageCode, args);
    String userMsg = messagesService.getUserMessage(messageCode, args);
    LOGGER.error(techMsg);
    return creator.create(techMsg, userMsg, messageCode);
  }

  private <T extends IdmException> T createException(IdmExceptionWithCauseCreator<T> creator,
      MessageCode messageCode, Throwable cause, Object... args) {
    String techMsg = messagesService.getTechMessage(messageCode, args);
    String userMsg = messagesService.getUserMessage(messageCode, args);
    LOGGER.error(techMsg);
    return creator.create(techMsg, userMsg, messageCode, cause);
  }

  @Autowired
  public void setMessagesService(MessagesService messagesService) {
    this.messagesService = messagesService;
  }
}
