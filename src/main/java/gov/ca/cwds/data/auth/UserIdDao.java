package gov.ca.cwds.data.auth;

import gov.ca.cwds.data.persistence.auth.UserId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * DAO for {@link UserId}.
 *
 * @author CWDS API Team
 */

@Repository
public interface UserIdDao extends ReadOnlyRepository<UserId, String> {

  @Query("SELECT U FROM UserId U WHERE U.logonId = :logonId AND U.endDate is null")
  List<UserId> findActiveByLogonId(@Param("logonId") String logonId);

  @Query("SELECT U FROM UserId U WHERE U.logonId in :logonIds AND U.endDate is null")
  List<UserId> findActiveByLogonIdIn(@Param("logonIds") Collection<String> logonIds);



}
