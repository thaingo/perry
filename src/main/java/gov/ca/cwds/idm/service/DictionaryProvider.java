package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.persistence.PermissionRepository;
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
  public List<String> getPermissions() {
    return permissionRepository.findPermissionNames();
  }

  @Autowired
  public void setPermissionRepository(PermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }
}
