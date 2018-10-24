package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import gov.ca.cwds.idm.persistence.ns.entity.UserNs;
import gov.ca.cwds.idm.persistence.ns.repository.UserNsRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    List<UserNs> userList =  userNsRepository.findByUsername(username);

    UserNs userNsEntity;

    if(userList.isEmpty()) {
      userNsEntity = new UserNs();
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

    List<UserNs> userList =  userNsRepository.findByUsername(username);

    if(userList.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(userList.get(0).getLastLoginTime());
    }
  }

  @Transactional(value = TOKEN_TRANSACTION_MANAGER, readOnly = true)
  public Set<UserNs> findByUsernames(Set<String> usernames) {
    return userNsRepository.findByUsernames(usernames);
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
