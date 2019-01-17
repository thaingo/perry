package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.config.api.idm.Roles;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public class RolesUserAttributeDiff extends CollectionUserAttributeDiff {

  public RolesUserAttributeDiff(
      AttributeType attributeType,
      Set<String> oldValue, Set<String> newValue) {
    super(attributeType, oldValue, newValue);
  }

  RolesUserAttributeDiff() {
  }

  @Override
  public String getOldValueAsString() {
    return getCollectionValueAsString(replaceRoleIdByName(getOldValue()));
  }

  @Override
  public String getNewValueAsString() {
    return getCollectionValueAsString(replaceRoleIdByName(getNewValue()));
  }

  private static Set<String> replaceRoleIdByName(Set<String> roleIds) {
    return roleIds != null ? roleIds.stream().map(Roles::getRoleNameById)
        .collect(Collectors.toSet()) : null;
  }

}
