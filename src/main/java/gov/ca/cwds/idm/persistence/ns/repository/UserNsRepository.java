package gov.ca.cwds.idm.persistence.ns.repository;

import gov.ca.cwds.idm.persistence.ns.entity.UserNsEntity;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Profile("idm")
@Repository
@SuppressWarnings({"squid:S1214"})//implementation details are queries and they are put here by Spring Data design
public interface UserNsRepository extends CrudRepository<UserNsEntity, Long> {

  String USERNAME = "username";

  @Query("select u from UserNsEntity u where u.username = :" + USERNAME)
  List<UserNsEntity> findByUsername(@Param(USERNAME)String username);
}
