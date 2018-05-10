package gov.ca.cwds.idm.persistence;

import gov.ca.cwds.data.auth.ReadOnlyRepository;
import gov.ca.cwds.idm.persistence.model.Permission;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface PermissionRepository extends ReadOnlyRepository<Permission, String> {

  @Query("SELECT p.name FROM Permission p order by p.name asc")
  List<String> findPermissionNames();

}
