package gov.ca.cwds.idm.service.cognito.attribute.diff.builder;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.StringUserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class StringAttributeDiffBuilder extends AbstractUserAttributeDiffBuilder<String> {

  public StringAttributeDiffBuilder(UserAttribute userAttribute, UserType userType,
      String newValue) {
    super(userAttribute, userType, newValue);
  }

  @Override
  public boolean doesDiffExist() {
    return doesDiffExist(getNewValue(),
        StringUserAttributeDiff.getOldValue(getUserType(), getUserAttribute()));
  }

  @Override
  public UserAttributeDiff<String> buildDiff() {
    return new StringUserAttributeDiff(getUserAttribute(), getUserType(), getNewValue());
  }

}
