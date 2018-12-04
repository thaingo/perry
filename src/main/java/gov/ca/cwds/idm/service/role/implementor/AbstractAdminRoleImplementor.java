package gov.ca.cwds.idm.service.role.implementor;

import static java.util.stream.Collectors.toList;

import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.DictionaryProvider;
import java.util.List;

public abstract class AbstractAdminRoleImplementor implements AdminRoleImplementor {

  private static final String CANS_PERMISSION_NAME = "CANS-rollout";

  private DictionaryProvider dictionaryProvider;

  @Override
  public List<String> getPossibleUserPermissions(boolean isRacfidUser) {
    List<String> allPermissionNames = getAllPermissionNames();

    if(isRacfidUser) {
      return allPermissionNames;
    } else {
      return allPermissionNames.stream()
          .filter(name -> !name.equals(CANS_PERMISSION_NAME))
          .collect(toList());
    }
  }

  private List<String> getAllPermissionNames() {
    return dictionaryProvider.getPermissions().stream()
        .map(Permission::getName).collect(toList());
  }

  public void setDictionaryProvider(DictionaryProvider dictionaryProvider) {
    this.dictionaryProvider = dictionaryProvider;
  }
}
