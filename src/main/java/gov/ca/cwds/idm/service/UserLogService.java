package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.persistence.model.OperationType.CREATE;
import static gov.ca.cwds.idm.persistence.model.OperationType.UPDATE;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_LOG_IDM_USER;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_LOG_IDM_USER_CREATE;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_LOG_IDM_USER_UPDATE;

import gov.ca.cwds.idm.dto.UserIdAndOperation;
import gov.ca.cwds.idm.persistence.UserLogRepository;
import gov.ca.cwds.idm.persistence.model.OperationType;
import gov.ca.cwds.idm.persistence.model.UserLog;
import gov.ca.cwds.service.messages.MessagesService;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("idm")
public class UserLogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserLogService.class);

  private UserLogRepository userLogRepository;
  private  MessagesService messages;

  @Transactional(value = "tokenTransactionManager")
  public Optional<UserLog> logCreate(String username) {
    return log(username, CREATE);
  }

  @Transactional(value = "tokenTransactionManager")
  public Optional<UserLog> logUpdate(String username) {
    return log(username, UPDATE);
  }

  public Set<UserIdAndOperation> getUserIdAndOperation(Date lastJobTime) {
    if(lastJobTime == null) {
      throw new IllegalArgumentException("Last Job Time cannot be null");
    }
    Set<UserIdAndOperation> idAndOperationSet = new HashSet<>();

    List<Object[]> iDAndOperationPairs = userLogRepository.getUserIdAndOperationTypes(lastJobTime);

    for(Object[] iDAndOperationPair :  iDAndOperationPairs) {
      String id = (String)iDAndOperationPair[0];
      OperationType operationType  = (OperationType)iDAndOperationPair[1];


      idAndOperationSet.add(new UserIdAndOperation(id, operationType));
    }

    return idAndOperationSet;
  }

  private Optional<UserLog> log(String username, OperationType operationType) {
    UserLog userLog = new UserLog();
    userLog.setUsername(username);
    userLog.setOperationType(operationType);
    userLog.setOperationTime(new Date());

    UserLog result = null;
    try {
      result = userLogRepository.save(userLog);
    } catch (Exception e) {

      String msg;
      if (operationType == OperationType.CREATE) {
        msg = messages.get(UNABLE_LOG_IDM_USER_CREATE, username);
      } else if (operationType == OperationType.UPDATE) {
        msg = messages.get(UNABLE_LOG_IDM_USER_UPDATE, username);
      } else {
        msg = messages.get(UNABLE_LOG_IDM_USER, operationType.toString(), username);
      }
      LOGGER.error(msg, e);
    }
    return Optional.ofNullable(result);
  }

  @Autowired
  public void setUserLogRepository(UserLogRepository userLogRepository) {
    this.userLogRepository = userLogRepository;
  }

  @Autowired
  public void setMessages(MessagesService messages) {
    this.messages = messages;
  }
}
