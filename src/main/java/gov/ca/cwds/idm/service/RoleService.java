package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.persistence.RoleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

  private RoleRepository roleRepository;

  @Transactional(value = "tokenTransactionManager", readOnly = true)
  public List<String> getRoleNames() {
    return roleRepository.findRoleNames();
  }

  @Autowired
  public void setRoleRepository(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }
}
