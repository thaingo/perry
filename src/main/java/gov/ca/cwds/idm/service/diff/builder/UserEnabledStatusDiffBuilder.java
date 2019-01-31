package gov.ca.cwds.idm.service.diff.builder;

import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.diff.UserAttributeDiff;
import gov.ca.cwds.idm.service.diff.UserEnabledStatusAttributeDiff;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class UserEnabledStatusDiffBuilder extends AbstractUserAttributeDiffBuilder<Boolean> {

  public UserEnabledStatusDiffBuilder(UserAttribute userAttribute, Boolean oldValue,
      Boolean newValue) {
    super(userAttribute, oldValue, newValue);
  }

  @Override
  public UserAttributeDiff<Boolean> buildDiff() {
    return new UserEnabledStatusAttributeDiff(getOldValue(), getNewValue());
  }
}
