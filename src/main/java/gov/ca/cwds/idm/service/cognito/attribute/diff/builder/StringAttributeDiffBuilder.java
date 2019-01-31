package gov.ca.cwds.idm.service.cognito.attribute.diff.builder;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.StringUserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class StringAttributeDiffBuilder extends AbstractUserAttributeDiffBuilder<String> {

  public StringAttributeDiffBuilder(UserAttribute userAttribute, User user, String oldValue,
      String newValue) {
    super(userAttribute, user, oldValue, newValue);
  }


  @Override
  public UserAttributeDiff<String> buildDiff() {
    return new StringUserAttributeDiff(getUserAttribute(), getUser(), getOldValue(), getNewValue());
  }

}
