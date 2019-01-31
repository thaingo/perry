package gov.ca.cwds.idm.service.cognito.attribute.diff;

import static gov.ca.cwds.config.api.idm.Roles.replaceRoleIdByName;

import gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute;
import java.util.Set;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public class RolesUserAttributeDiff extends CollectionUserAttributeDiff {

  public RolesUserAttributeDiff(Set<String> oldValue, Set<String> newValue) {
    super(CustomUserAttribute.ROLES, oldValue, newValue);
  }

  @Override
  public String getOldValueAsString() {
    return getCollectionValueAsString(replaceRoleIdByName(getOldValue()));
  }

  @Override
  public String getNewValueAsString() {
    return getCollectionValueAsString(replaceRoleIdByName(getNewValue()));
  }
}
