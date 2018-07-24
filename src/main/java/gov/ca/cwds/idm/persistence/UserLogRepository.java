package gov.ca.cwds.idm.persistence;

import gov.ca.cwds.idm.persistence.model.UserLog;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

@Profile("idm")
public interface UserLogRepository extends CrudRepository<UserLog, Long> {

}
