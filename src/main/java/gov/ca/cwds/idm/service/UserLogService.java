package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.persistence.model.Operation.CREATE;
import static gov.ca.cwds.idm.persistence.model.Operation.UPDATE;

import gov.ca.cwds.idm.persistence.UserLogRepository;
import gov.ca.cwds.idm.persistence.model.Operation;
import gov.ca.cwds.idm.persistence.model.UserLog;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("idm")
public class UserLogService {

  private UserLogRepository userLogRepository;

  @Transactional(value = "tokenTransactionManager")
  public UserLog logCreate(String username) {
    return log(username, CREATE);
  }

  @Transactional(value = "tokenTransactionManager")
  public UserLog logUpdate(String username) {
    return log(username, UPDATE);
  }

  private UserLog log(String username, Operation operation) {
    UserLog userLog = new UserLog();
    userLog.setUsername(username);
    userLog.setOperation(operation);
    userLog.setOperationTime(new Date());
    return userLogRepository.save(userLog);
  }

  @Autowired
  public void setPermissionRepository(UserLogRepository userLogRepository) {
    this.userLogRepository = userLogRepository;
  }
}
