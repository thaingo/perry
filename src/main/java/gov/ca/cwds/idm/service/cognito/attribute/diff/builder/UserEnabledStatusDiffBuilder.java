package gov.ca.cwds.idm.service.cognito.attribute.diff.builder;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserEnabledStatusAttributeDiff;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class UserEnabledStatusDiffBuilder extends AbstractUserAttributeDiffBuilder<Boolean> {

  public UserEnabledStatusDiffBuilder(UserAttribute userAttribute, User user, Boolean oldValue,
      Boolean newValue) {
    super(userAttribute, user, oldValue, newValue);
  }

  @Override
  public UserAttributeDiff<Boolean> buildDiff() {
    return new UserEnabledStatusAttributeDiff(getUser(), getOldValue(), getNewValue());
  }
}
