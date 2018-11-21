package gov.ca.cwds.idm.service.exception;

import gov.ca.cwds.idm.exception.IdmException;
import gov.ca.cwds.idm.exception.PartialSuccessException;
import gov.ca.cwds.idm.exception.UserAlreadyExistsException;
import gov.ca.cwds.idm.exception.UserNotFoundException;
import gov.ca.cwds.idm.exception.UserValidationException;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.service.messages.MessagesService.Messages;
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

  public IdmException createIdmException(MessageCode messageCode, Throwable cause, String... args) {
    return createExceptionWithCause(IdmException::new, cause, messageCode, args);
  }

  public UserNotFoundException createUserNotFoundException(MessageCode messageCode, Throwable cause,
      String... args) {
    return createExceptionWithCause(UserNotFoundException::new, cause, messageCode, args);
  }

  public UserAlreadyExistsException createUserAlreadyExistsException(MessageCode messageCode,
      Throwable cause, String... args) {
    return createExceptionWithCause(UserAlreadyExistsException::new, cause, messageCode, args);
  }

  public UserValidationException createValidationException(MessageCode messageCode,
      String... args) {
    return createException(UserValidationException::new, messageCode, args);
  }

  public UserValidationException createValidationException(MessageCode messageCode, Throwable cause,
      String... args) {
    return createExceptionWithCause(UserValidationException::new, cause, messageCode, args);
  }

  public PartialSuccessException createPartialSuccessException(
      String userId, OperationType operationType, MessageCode errorCode, Exception... causes) {
    Messages messages = messagesService.getMessages(errorCode, userId);
    PartialSuccessException ex = new PartialSuccessException(
        userId, operationType, messages.getTechMsg(), messages.getUserMsg(), errorCode, causes);
    LOGGER.error(messages.getTechMsg(), ex);
    return ex;
  }

  private <T extends IdmException> T createException(IdmExceptionCreator<T> creator,
      MessageCode messageCode, String... args) {
    Messages messages = messagesService.getMessages(messageCode, args);
    T ex = creator.create(messages.getTechMsg(), messages.getUserMsg(), messageCode);
    LOGGER.error(messages.getTechMsg(), ex);
    return ex;
  }

  private <T extends IdmException> T createExceptionWithCause(IdmExceptionWithCauseCreator<T> creator,
      Throwable cause, MessageCode messageCode, String... args) {
    Messages messages = messagesService.getMessages(messageCode, args);
    T ex = creator.create(messages.getTechMsg(), messages.getUserMsg(), messageCode, cause);
    LOGGER.error(messages.getTechMsg(), ex);
    return ex;
  }

  @FunctionalInterface
  interface IdmExceptionCreator<T extends IdmException> {
    T create(String techMsg, String userMsg, MessageCode messageCode);
  }

  @FunctionalInterface
  interface IdmExceptionWithCauseCreator<T extends IdmException> {
    T create(String techMsg, String userMsg, MessageCode messageCode, Throwable cause);
  }

  @Autowired
  public void setMessagesService(MessagesService messagesService) {
    this.messagesService = messagesService;
  }
}
