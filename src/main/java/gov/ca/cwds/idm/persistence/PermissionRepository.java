package gov.ca.cwds.idm.persistence;

import gov.ca.cwds.data.auth.ReadOnlyRepository;
import gov.ca.cwds.idm.persistence.model.Permission;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@Profile("idm")
public interface PermissionRepository extends CrudRepository<Permission, String> {

  @Query("SELECT p.name FROM Permission p order by p.name asc")
  List<String> findPermissionNames();

}
