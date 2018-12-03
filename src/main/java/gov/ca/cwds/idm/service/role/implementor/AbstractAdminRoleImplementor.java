package gov.ca.cwds.idm.service.role.implementor;

import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.DictionaryProvider;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractAdminRoleImplementor implements AdminRoleImplementor {

  private DictionaryProvider dictionaryProvider;

  @Override
  public List<String> getPossibleUserPermissions(boolean isRacfidUser) {
    if(isRacfidUser) {
      return getAllPermissionNames();
    } else {
      return getAllPermissionNames();
    }
  }

  private List<String> getAllPermissionNames() {
    return dictionaryProvider.getPermissions().stream()
        .map(Permission::getName).collect(Collectors.toList());
  }

  public void setDictionaryProvider(DictionaryProvider dictionaryProvider) {
    this.dictionaryProvider = dictionaryProvider;
  }
}
