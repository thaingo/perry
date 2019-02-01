package gov.ca.cwds.idm.service.diff;

import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public class StringUserAttributeDiff extends UserAttributeDiff<String> {

  public StringUserAttributeDiff(UserAttribute userAttribute, String oldValue, String newValue) {
    super(userAttribute, oldValue, newValue);
  }

  @Override
  public String getOldValueAsString() {
    return getOldValue();
  }

  @Override
  public String getNewValueAsString() {
    return getNewValue();
  }
}
