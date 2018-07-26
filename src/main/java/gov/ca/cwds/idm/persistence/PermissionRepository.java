package gov.ca.cwds.idm.persistence;

import gov.ca.cwds.idm.persistence.model.Permission;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;

@Profile("idm")
public interface PermissionRepository extends ReadOnlyRepository<Permission, String> {

  @Query("SELECT p FROM Permission p order by p.description asc")
  List<Permission> findPermissions();
}
