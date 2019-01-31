package gov.ca.cwds.idm.service.diff.builder;

import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.diff.StringUserAttributeDiff;
import gov.ca.cwds.idm.service.diff.UserAttributeDiff;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class StringAttributeDiffBuilder extends AbstractUserAttributeDiffBuilder<String> {

  public StringAttributeDiffBuilder(UserAttribute userAttribute, String oldValue,
      String newValue) {
    super(userAttribute, oldValue, newValue);
  }


  @Override
  public UserAttributeDiff<String> buildDiff() {
    return new StringUserAttributeDiff(getUserAttribute(), getOldValue(), getNewValue());
  }

}
