package gov.ca.cwds.idm.persistence.ns.repository;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Profile("idm")
@Repository
@SuppressWarnings({"squid:S1214"})//implementation details are queries and they are put here by Spring Data design
public interface NsUserRepository extends CrudRepository<NsUser, Long> {

  String USERNAME = "username";
  String USERNAMES = "usernames";

  @Query("select u from NsUser u where u.username = :" + USERNAME)
  Set<NsUser> findByUsername(@Param(USERNAME)String username);

  @Query("select u from NsUser u where u.username in :" + USERNAMES)
  Set<NsUser> findByUsernames(@Param(USERNAMES) Set<String> usernames);
}
