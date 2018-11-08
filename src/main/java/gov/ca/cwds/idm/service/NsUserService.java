package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.persistence.ns.repository.NsUserRepository;
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
public class NsUserService {

  private NsUserRepository nsUserRepository;

  private UserLogService userLogService;

  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void saveLastLoginTime(String username, LocalDateTime loginTime) {
    NsUser nsUser = getOrCreateNewUser(username);
    nsUser.setLastLoginTime(loginTime);

    nsUserRepository.save(nsUser);
    userLogService.logUpdate(username, loginTime);
  }

  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void saveLastRegistrationResubmitTime(String username, LocalDateTime registrationResubmitTime) {
    NsUser nsUser = getOrCreateNewUser(username);
    nsUser.setLastRegistrationResubmitTime(registrationResubmitTime);

    nsUserRepository.save(nsUser);
    userLogService.logUpdate(username, registrationResubmitTime);
  }

  private NsUser getOrCreateNewUser(String username) {
    List<NsUser> userList =  nsUserRepository.findByUsername(username);

    NsUser nsUser;

    if(userList.isEmpty()) {
      nsUser = new NsUser();
      nsUser.setUsername(username);
    } else {
      nsUser = userList.get(0);
    }
    return nsUser;
  }

  @Transactional(value = TOKEN_TRANSACTION_MANAGER, readOnly = true)
  public Optional<LocalDateTime> getLastLoginTime(String username) {

    List<NsUser> userList =  nsUserRepository.findByUsername(username);

    if(userList.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(userList.get(0).getLastLoginTime());
    }
  }

  @Transactional(value = TOKEN_TRANSACTION_MANAGER, readOnly = true)
  public Set<NsUser> findByUsernames(Set<String> usernames) {
    return nsUserRepository.findByUsernames(usernames);
  }

  @Autowired
  public void setNsUserRepository(NsUserRepository nsUserRepository) {
    this.nsUserRepository = nsUserRepository;
  }

  @Autowired
  public void setUserLogService(UserLogService userLogService) {
    this.userLogService = userLogService;
  }
}
