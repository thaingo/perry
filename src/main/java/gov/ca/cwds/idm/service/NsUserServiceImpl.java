package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.persistence.ns.repository.NsUserRepository;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
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
public class NsUserServiceImpl implements NsUserService {

  private NsUserRepository nsUserRepository;

  @Override
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void create(NsUser nsUser) {
    nsUserRepository.save(nsUser);
  }

  @Override
  @Transactional(value = TOKEN_TRANSACTION_MANAGER, readOnly = true)
  public Optional<NsUser> findByUsername(String username) {

    if (username == null) {
      throw new IllegalArgumentException("username is null");
    }

    List<NsUser> userList = nsUserRepository.findByUsername(username);

    if (userList.size() > 1) {
      throw new IllegalStateException(
          "more then one user with username " + username + " are found");
    }

    if (userList.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(userList.get(0));
  }

  @Override
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void saveLastLoginTime(String username, LocalDateTime loginTime) {
    NsUser nsUser = getOrCreateNewNsUser(username);
    nsUser.setLastLoginTime(loginTime);
    nsUserRepository.save(nsUser);
  }

  @Override
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public boolean update(UserUpdateRequest userUpdateRequest) {
    UpdateDifference updateDifference = userUpdateRequest.getUpdateDifference();

    NsUser nsUser = getOrCreateNewNsUser(userUpdateRequest.getUserId());

    NsUserBuilder nsUserBuilder = new NsUserBuilder(nsUser, updateDifference);
    NsUser modifiedNsUser = nsUserBuilder.build();

    if (nsUserBuilder.userIsUpdated()) {
      modifiedNsUser.setLastModifiedTime(LocalDateTime.now());
      nsUserRepository.save(modifiedNsUser);
      return true;
    } else {
      return false;
    }
  }

  private NsUser getOrCreateNewNsUser(String username) {
    Optional<NsUser> nsUserOpt = findByUsername(username);

    if (nsUserOpt.isPresent()) {
      return nsUserOpt.get();
    } else {
      NsUser nsUser = new NsUser();
      nsUser.setUsername(username);
      return nsUser;
    }
  }

  @Override
  @Transactional(value = TOKEN_TRANSACTION_MANAGER, readOnly = true)
  public List<NsUser> findByUsernames(Set<String> usernames) {
    return nsUserRepository.findByUsernames(usernames);
  }

  @Autowired
  public void setNsUserRepository(NsUserRepository nsUserRepository) {
    this.nsUserRepository = nsUserRepository;
  }
}
