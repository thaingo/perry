package gov.ca.cwds.idm.persistence.ns.repository;

import gov.ca.cwds.data.auth.ReadOnlyRepository;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Profile("idm")
@Repository
public interface PermissionRepository extends ReadOnlyRepository<Permission, String> {

  @Query("SELECT p FROM Permission p order by p.description asc")
  List<Permission> findPermissions();
}
