package gov.ca.cwds.idm.service.cognito.attribute.diff.builder;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.RolesUserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import java.util.Set;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class RoleAttributeDiffBuilder extends CollectionAttributeDiffBuilder {

  public RoleAttributeDiffBuilder(User user, Set<String> oldValue, Set<String> newValue) {
    super(CustomUserAttribute.ROLES, user, oldValue, newValue);
  }

  @Override
  public UserAttributeDiff<Set<String>> buildDiff() {
    return new RolesUserAttributeDiff(getUser(), getOldValue(), getNewValue());
  }

}
