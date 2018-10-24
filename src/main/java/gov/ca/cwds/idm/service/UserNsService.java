package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import gov.ca.cwds.idm.persistence.ns.entity.UserNsEntity;
import gov.ca.cwds.idm.persistence.ns.repository.UserNsRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("idm")
public class UserNsService {

  private UserNsRepository userNsRepository;

  private UserLogService userLogService;

  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void saveLastLoginTime(String username, LocalDateTime loginTime) {
    List<UserNsEntity> userList =  userNsRepository.findByUsername(username);

    UserNsEntity userNsEntity;

    if(userList.isEmpty()) {
      userNsEntity = new UserNsEntity();
      userNsEntity.setUsername(username);
    } else {
      userNsEntity = userList.get(0);
    }
    userNsEntity.setLastLoginTime(loginTime);

    userNsRepository.save(userNsEntity);
    userLogService.logUpdate(username);
  }

  @Transactional(value = TOKEN_TRANSACTION_MANAGER, readOnly = true)
  public Optional<LocalDateTime> getLastLoginTime(String username) {

    List<UserNsEntity> userList =  userNsRepository.findByUsername(username);

    if(userList.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(userList.get(0).getLastLoginTime());
    }
  }

  @Autowired
  public void setUserNsRepository(UserNsRepository userNsRepository) {
    this.userNsRepository = userNsRepository;
  }

  @Autowired
  public void setUserLogService(UserLogService userLogService) {
    this.userLogService = userLogService;
  }
}
