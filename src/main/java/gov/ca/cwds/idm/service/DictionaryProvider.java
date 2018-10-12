package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.persistence.ns.repository.PermissionRepository;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("idm")
public class DictionaryProvider {

  private PermissionRepository permissionRepository;

  @Transactional(value = TOKEN_TRANSACTION_MANAGER, readOnly = true)
  public List<Permission> getPermissions() {
    return permissionRepository.findPermissions();
  }

  @Autowired
  public void setPermissionRepository(PermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }

  public List<Map<String,String>> getRoles() {
    return  Roles.findRoles();
  }
}
