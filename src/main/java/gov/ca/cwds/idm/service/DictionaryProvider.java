package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.persistence.PermissionRepository;
import gov.ca.cwds.idm.persistence.model.Permission;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("idm")
public class DictionaryProvider {

  private PermissionRepository permissionRepository;

  @Transactional(value = "tokenTransactionManager", readOnly = true)
  public List<Permission> getPermissions() {
    return permissionRepository.findPermissions();
  }

  @Autowired
  public void setPermissionRepository(PermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }

  @Transactional(value = "tokenTransactionManager")
  public void overwritePermissions(List<Permission> permissions) {
    permissionRepository.deleteAll();
    permissionRepository.save(permissions);
  }
}
