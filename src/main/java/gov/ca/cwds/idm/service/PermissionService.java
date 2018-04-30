package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.persistence.PermissionRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {

  private PermissionRepository permissionRepository;

  @Transactional(value = "tokenTransactionManager", readOnly = true)
  public List<String> getPermissionNames() {
    return permissionRepository.findPermissionNames();
  }

  @Autowired
  public void setPermissionRepository(PermissionRepository roleRepository) {
    this.permissionRepository = roleRepository;
  }
}
