package gov.ca.cwds.idm.persistence;

import gov.ca.cwds.idm.persistence.model.UserLog;
import java.util.Date;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Profile("idm")
@Transactional(value = "tokenTransactionManager")
public interface UserLogRepository extends CrudRepository<UserLog, Long> {

  @Override
  UserLog save(UserLog entity);

  @Query(
      "select u.username, u.operationType from UserLog u "
          + "where u.operationTime > :lastDate "
          + "group by u.username, u.operationType")
  @Transactional(readOnly = true)
  List<Object[]> getUserIdAndOperationTypes(@Param("lastDate") Date lastDate);

  @Query("delete from UserLog u where u.operationTime <= :lastDate")
  @Modifying
  int deleteLogsBeforeDate(@Param("lastDate") Date lastDate);
}
