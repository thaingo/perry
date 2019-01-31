package gov.ca.cwds.idm.service.diff.builder;

import gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute;
import gov.ca.cwds.idm.service.diff.RolesUserAttributeDiff;
import gov.ca.cwds.idm.service.diff.UserAttributeDiff;
import java.util.Set;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class RoleAttributeDiffBuilder extends CollectionAttributeDiffBuilder {

  public RoleAttributeDiffBuilder(Set<String> oldValue, Set<String> newValue) {
    super(CustomUserAttribute.ROLES, oldValue, newValue);
  }

  @Override
  public UserAttributeDiff<Set<String>> buildDiff() {
    return new RolesUserAttributeDiff(getOldValue(), getNewValue());
  }

}
