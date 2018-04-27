package gov.ca.cwds.idm.persistence;

import gov.ca.cwds.data.auth.ReadOnlyRepository;
import org.springframework.data.jpa.repository.Query;
import gov.ca.cwds.idm.persistence.model.Role;
import java.util.List;

public interface RoleRepository extends ReadOnlyRepository<Role, String> {
  @Query("SELECT r.name FROM Role r order by r.name asc")
  List<String> findRoleNames();
}
