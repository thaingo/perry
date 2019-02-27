package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NsUserService {

  void create(NsUser nsUser);

  boolean update(UserUpdateRequest userUpdateRequest);

  void saveLastLoginTime(String username, LocalDateTime loginTime);

  Optional<NsUser> findByUsername(String username);

  List<NsUser> findByUsernames(Set<String> usernames);

  List<NsUser> findByRacfids(Set<String> racfids);
}
