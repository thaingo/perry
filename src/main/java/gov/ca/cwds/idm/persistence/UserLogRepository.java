package gov.ca.cwds.idm.persistence;

import gov.ca.cwds.idm.persistence.model.UserLog;
import java.util.Date;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@Profile("idm")
public interface UserLogRepository extends CrudRepository<UserLog, Long> {

  @Query(
      "select u.username, u.operationType from USER u "
          + "where u.operationTime > :lastJobTime "
          + "group by u.username, u.operationType")
  List<Object[]> getUserIdAndOperationTypes(Date lastJobTime);
}
