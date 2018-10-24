package gov.ca.cwds.idm.persistence.ns.repository;

import gov.ca.cwds.idm.persistence.ns.entity.UserNs;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Profile("idm")
@Repository
@SuppressWarnings({"squid:S1214"})//implementation details are queries and they are put here by Spring Data design
public interface UserNsRepository extends CrudRepository<UserNs, Long> {

  String USERNAME = "username";
  String USERNAMES = "usernames";

  @Query("select u from UserNs u where u.username = :" + USERNAME)
  List<UserNs> findByUsername(@Param(USERNAME)String username);

  @Query("select u from UserNs u where u.username in :" + USERNAMES)
  Set<UserNs> findByUsernames(@Param(USERNAMES) Set<String> usernames);
}
