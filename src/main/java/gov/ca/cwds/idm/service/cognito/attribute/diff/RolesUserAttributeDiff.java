package gov.ca.cwds.idm.service.cognito.attribute.diff;

import static gov.ca.cwds.config.api.idm.Roles.replaceRoleIdByName;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute;
import java.util.Set;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public class RolesUserAttributeDiff extends CollectionUserAttributeDiff {

  public RolesUserAttributeDiff(UserType existingUser, Set<String> newValue) {
    super(CustomUserAttribute.ROLES, existingUser, newValue);
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
