package gov.ca.cwds.data.auth;

import gov.ca.cwds.data.persistence.auth.UserId;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * DAO for {@link UserId}.
 *
 * @author CWDS API Team
 */

@Repository
public interface UserIdDao extends ReadOnlyRepository<UserId, String> {

  @Query("SELECT U FROM UserId U "
      + "LEFT OUTER JOIN FETCH U.staffPerson sp "
      + "LEFT OUTER JOIN FETCH sp.office "
      + "WHERE U.logonId in :logonIds "
      + "AND U.endDate is null")
  Set<UserId> findActiveByLogonIdIn(@Param("logonIds") Collection<String> logonIds);

  @Query("SELECT u FROM UserId u "
      + "LEFT OUTER JOIN FETCH u.staffPerson sp "
      + "LEFT OUTER JOIN FETCH sp.unitAuthorities ua "
      + "LEFT OUTER JOIN FETCH ua.assignmentUnit "
      + "LEFT OUTER JOIN FETCH sp.office "
      + "LEFT OUTER JOIN FETCH u.privileges p "
      + "WHERE u.logonId = :logonId "
      + "AND u.endDate IS NULL ")
  Set<UserId> findActiveByLogonId(@Param("logonId") String logonId);

  @Query(
      value = "SELECT count (asg.ESTBLSH_ID) as assignments"
          + " FROM ASGNM_T as asg"
          + " JOIN CASE_LDT as csl ON asg.FKCASE_LDT = csl.IDENTIFIER"
          + " JOIN CSLDWGHT as spcl ON csl.IDENTIFIER = spcl.FKCASE_LDT"
          + " JOIN STFPERST as sp ON spcl.FKSTFPERST = sp.IDENTIFIER"
          + " LEFT OUTER JOIN REFERL_T as r"
          + "   ON"
          + "     asg.ESTBLSH_ID = r.IDENTIFIER"
          + "     AND asg.ESTBLSH_CD = 'R'"
          + " LEFT OUTER JOIN CASE_T as c "
          + "   ON"
          + "     asg.ESTBLSH_ID = c.IDENTIFIER "
          + "     AND asg.ESTBLSH_CD = 'C' "
          + " WHERE"
          + "    asg.END_DT IS NULL"
          + "    AND csl.END_DT IS NULL"
          + "    AND REFCLSR_DT IS NULL"
          + "    AND c.END_DT IS NULL"
          + "    AND sp.IDENTIFIER = :staffPersonId",
      nativeQuery = true
  )
  int assignmentsCount(@Param("staffPersonId") String staffPersonId);

}
