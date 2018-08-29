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
@SuppressWarnings({"squid:S1214"})//implementation details are queries and they are put here by Spring Data design
public interface UserLogRepository extends CrudRepository<UserLog, Long> {

  String LAST_DATE = "lastDate";

  @Override
  UserLog save(UserLog entity);

  @Query(
      "select u.username, u.operationType from UserLog u "
          + "where u.operationTime > :" + LAST_DATE
          + " group by u.username, u.operationType")
  @Transactional(readOnly = true)
  List<Object[]> getUserIdAndOperationTypes(@Param(LAST_DATE) Date lastDate);

  @Query("delete from UserLog u where u.operationTime <= :" + LAST_DATE)
  @Modifying
  int deleteLogsBeforeDate(@Param(LAST_DATE) Date lastDate);
}
