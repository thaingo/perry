package gov.ca.cwds.idm.service.cognito.attribute.diff.builder;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAccountStatusAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class AccountStatusDiffBuilder extends AbstractUserAttributeDiffBuilder<Boolean> {

  public AccountStatusDiffBuilder(UserAttribute userAttribute, UserType userType,
      Boolean newValue) {
    super(userAttribute, userType, newValue);
  }

  @Override
  public boolean doesDiffExist() {
    return doesDiffExist(getNewValue(),
        UserAccountStatusAttributeDiff.getOldValue(getUserType()));
  }

  @Override
  public UserAttributeDiff<Boolean> buildDiff() {
    return new UserAccountStatusAttributeDiff(getUserType(), getNewValue());
  }

}
