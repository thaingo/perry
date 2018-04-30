package gov.ca.cwds.idm.persistence;

import gov.ca.cwds.idm.persistence.model.Permission;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PermissionRepository extends CrudRepository<Permission, String> {

  @Query("SELECT p.name FROM Permission p order by p.name asc")
  @Cacheable("permissionNames")
  List<String> findPermissionNames();

  @Override
  @CacheEvict(value = "permissionNames")
  <S extends Permission> S save(S entity);

  @Override
  @CacheEvict(value = "permissionNames")
  void delete(String name);
}
