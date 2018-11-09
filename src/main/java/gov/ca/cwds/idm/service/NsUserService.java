package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.persistence.ns.repository.NsUserRepository;
import java.time.LocalDateTime;
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

  @Transactional(value = TOKEN_TRANSACTION_MANAGER, readOnly = true)
  public Optional<NsUser> findByUsername(String username) {

    Set<NsUser> userSet =  nsUserRepository.findByUsername(username);

    if(username == null) {
      throw new IllegalArgumentException("username is null");
    }

    if(userSet.size() > 1) {
      throw new IllegalStateException(
          "more then one user with username " + username + " are found");
    }

    if(userSet.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(userSet.iterator().next());
  }

  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void saveLastLoginTime(String username, LocalDateTime loginTime) {
    NsUser nsUser = getOrCreateNewNsUser(username);

    nsUser.setLastLoginTime(loginTime);
    nsUserRepository.save(nsUser);
    userLogService.logUpdate(username, loginTime);
  }

  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void saveLastRegistrationResubmitTime(String username,
      LocalDateTime registrationResubmitTime) {
    NsUser nsUser = getOrCreateNewNsUser(username);

    nsUser.setLastRegistrationResubmitTime(registrationResubmitTime);
    nsUserRepository.save(nsUser);
    userLogService.logUpdate(username, registrationResubmitTime);
  }

  private NsUser getOrCreateNewNsUser(String username) {
    Optional<NsUser> nsUserOpt = findByUsername(username);

    if(nsUserOpt.isPresent()) {
      return nsUserOpt.get();
    } else {
      NsUser nsUser = new NsUser();
      nsUser.setUsername(username);
      return nsUser;
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
