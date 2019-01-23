package gov.ca.cwds.idm.service.cognito.attribute.diff.builder;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserEnabledStatusAttributeDiff;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class UserEnabledStatusDiffBuilder extends AbstractUserAttributeDiffBuilder<Boolean> {

  public UserEnabledStatusDiffBuilder(UserAttribute userAttribute, UserType userType,
      Boolean newValue) {
    super(userAttribute, userType, newValue);
  }

  @Override
  public boolean doesDiffExist() {
    return doesDiffExist(getNewValue(),
        UserEnabledStatusAttributeDiff.getOldValue(getUserType()));
  }

  @Override
  public UserAttributeDiff<Boolean> buildDiff() {
    return new UserEnabledStatusAttributeDiff(getUserType(), getNewValue());
  }

}
