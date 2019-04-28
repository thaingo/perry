package gov.ca.cwds.idm.service;

import static java.util.stream.Collectors.toList;

import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class PossibleUserPermissionsService {

  public static final String CANS_PERMISSION_NAME = "CANS-rollout";
  public static final String SNAPSHOT_PERMISSION_NAME = "Snapshot-rollout";

  private DictionaryProvider dictionaryProvider;

  public List<String> getPossibleUserPermissions(boolean isRacfidUser) {
    List<String> allPermissionNames = getAllPermissionNames();
    allPermissionNames = allPermissionNames.stream()
        .filter(name -> !SNAPSHOT_PERMISSION_NAME.equals(name))
        .collect(toList());

    if(isRacfidUser) {
      return allPermissionNames;
    } else {
      return allPermissionNames.stream()
          .filter(name -> !CANS_PERMISSION_NAME.equals(name))
          .collect(toList());
    }
  }

  private List<String> getAllPermissionNames() {
    return dictionaryProvider.getPermissions().stream()
        .map(Permission::getName).collect(toList());
  }

  @Autowired
  public void setDictionaryProvider(DictionaryProvider dictionaryProvider) {
    this.dictionaryProvider = dictionaryProvider;
  }
}
