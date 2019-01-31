package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.idm.service.cognito.attribute.DatabaseUserAttribute.NOTES;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.persistence.ns.repository.NsUserRepository;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.diff.Diff;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
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

    if(username == null) {
      throw new IllegalArgumentException("username is null");
    }

    List<NsUser> userList =  nsUserRepository.findByUsername(username);

    if(userList.size() > 1) {
      throw new IllegalStateException(
          "more then one user with username " + username + " are found");
    }

    if(userList.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(userList.get(0));
  }

  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void saveLastLoginTime(String username, LocalDateTime loginTime) {
    NsUser nsUser = getOrCreateNewNsUser(username);
    nsUser.setLastLoginTime(loginTime);
    nsUserRepository.save(nsUser);
    userLogService.logUpdate(username, loginTime);
  }

  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public boolean update(UserUpdateRequest userUpdateRequest) {
    Map<UserAttribute, Diff> databaseDiffMap =  userUpdateRequest.getDatabaseDiffMap();

    if(databaseDiffMap.isEmpty()){
      return false;
    }
    NsUser nsUser = getOrCreateNewNsUser(userUpdateRequest.getUserId());

    NsUser modifiedNsUser = new NsUserBuilder(nsUser, databaseDiffMap).build();

    nsUserRepository.save(modifiedNsUser);
    return true;
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
  public List<NsUser> findByUsernames(Set<String> usernames) {
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

  public static class NsUserBuilder {
    private final NsUser nsUser;
    private final Map<UserAttribute, Diff> databaseDiffMap;

    public NsUserBuilder(NsUser nsUser, Map<UserAttribute, Diff> databaseDiffMap) {
      this.nsUser = nsUser;
      this.databaseDiffMap = databaseDiffMap;
    }

    public NsUser build() {
      setProperty(NOTES, nsUser::setNotes);
      return nsUser;
    }

    private <T> void setProperty(UserAttribute userAttribute, Consumer<T> setter) {
      if(databaseDiffMap.containsKey(userAttribute)) {
        T newValue = (T)(databaseDiffMap.get(userAttribute).getNewValue());
        setter.accept(newValue);
      }
    }
  }
}
