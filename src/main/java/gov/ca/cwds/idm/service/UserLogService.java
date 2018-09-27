package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.persistence.ns.OperationType.CREATE;
import static gov.ca.cwds.idm.persistence.ns.OperationType.UPDATE;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_LOG_IDM_USER;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_LOG_IDM_USER_CREATE;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_LOG_IDM_USER_UPDATE;

import gov.ca.cwds.idm.dto.UserIdAndOperation;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import gov.ca.cwds.idm.service.execution.OptionalExecution;
import gov.ca.cwds.service.messages.MessagesService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class UserLogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserLogService.class);

  private UserLogTransactionalService userLogTransactionalService;
  private MessagesService messages;

  public OptionalExecution<String, UserLog> logCreate(String username) {
    return log(username, CREATE);
  }

  public OptionalExecution<String, UserLog> logUpdate(String username) {
    return log(username, UPDATE);
  }

  @SuppressWarnings({"fb-contrib:CLI_CONSTANT_LIST_INDEX"})
  public List<UserIdAndOperation> getUserIdAndOperations(LocalDateTime lastJobTime) {
    if (lastJobTime == null) {
      throw new IllegalArgumentException("Last Job Time cannot be null");
    }

    List<Object[]> iDAndOperationPairs = userLogTransactionalService.getUserIdAndOperationTypes(lastJobTime);

    return iDAndOperationPairs
        .stream()
        .map(e -> new UserIdAndOperation((String) e[0], (OperationType) e[1]))
        .collect(Collectors.toList());
  }

  public int deleteProcessedLogs(LocalDateTime lastJobTime) {
    return userLogTransactionalService.deleteLogsBeforeDate(lastJobTime);
  }

  private OptionalExecution<String, UserLog> log(String username, OperationType operationType) {

    return new OptionalExecution<String, UserLog>(username){
      @Override
      protected UserLog tryMethod(String username) {
        UserLog userLog = new UserLog();
        userLog.setUsername(username);
        userLog.setOperationType(operationType);
        userLog.setOperationTime(LocalDateTime.now());

        return userLogTransactionalService.save(userLog);
      }
      @Override
      protected void catchMethod(Exception e) {
        String msg = getErrorMessage(username, operationType);
        LOGGER.error(msg, e);
      }
    };
  }

  private String getErrorMessage(String username, OperationType operationType) {
    String msg;
    if (operationType == OperationType.CREATE) {
      msg = messages.getTech(UNABLE_LOG_IDM_USER_CREATE, username);
    } else if (operationType == OperationType.UPDATE) {
      msg = messages.getTech(UNABLE_LOG_IDM_USER_UPDATE, username);
    } else {
      msg = messages.getTech(UNABLE_LOG_IDM_USER, operationType.toString(), username);
    }
    return msg;
  }

  @Autowired
  public void setUserLogTransactionalService(
      UserLogTransactionalService userLogTransactionalService) {
    this.userLogTransactionalService = userLogTransactionalService;
  }

  @Autowired
  public void setMessages(MessagesService messages) {
    this.messages = messages;
  }
}
