package gov.ca.cwds.data.auth;

import gov.ca.cwds.data.persistence.auth.UserId;
import java.util.Set;
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

  @Query("SELECT u FROM UserId u "
      + "left outer join fetch u.staffPerson sp "
      + "left outer join fetch sp.unitAuthorities ua "
      + "left outer join fetch ua.assignmentUnit "
      + "left outer join fetch sp.office "
      + "left outer join fetch u.privileges "
      + "WHERE u.logonId = :logonId AND u.endDate is null")
  Set<UserId> findActiveByLogonId2(@Param("logonId") String logonId);


}
