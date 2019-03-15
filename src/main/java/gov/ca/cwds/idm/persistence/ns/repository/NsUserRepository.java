package gov.ca.cwds.idm.persistence.ns.repository;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings({"squid:S1214"})//implementation details are queries and they are put here by Spring Data design
public interface NsUserRepository extends CrudRepository<NsUser, Long> {

  String USERNAME = "username";
  String USERNAMES = "usernames";
  String RACFIDS = "racfids";

  @Query("select u from NsUser u where u.username = :" + USERNAME)
  List<NsUser> findByUsername(@Param(USERNAME)String username);

  @Query("select u from NsUser u where u.username in :" + USERNAMES)
  List<NsUser> findByUsernames(@Param(USERNAMES) Set<String> usernames);

  @Query("select u from NsUser u where u.racfid in :" + RACFIDS)
  List<NsUser> findByRacfids(@Param(RACFIDS) Set<String> racfids);
}
