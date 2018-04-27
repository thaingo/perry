package gov.ca.cwds.idm.persistence;

import gov.ca.cwds.idm.persistence.model.Role;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, String> {

  @Query("SELECT r.name FROM Role r order by r.name asc")
  @Cacheable("roleNames")
  List<String> findRoleNames();

  @Override
  @CacheEvict(value = "roleNames")
  <S extends Role> S save(S entity);

  @Override
  @CacheEvict(value = "roleNames")
  void delete(String name);
}
