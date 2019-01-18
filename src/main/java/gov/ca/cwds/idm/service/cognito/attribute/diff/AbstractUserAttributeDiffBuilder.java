package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
 abstract class AbstractUserAttributeDiffBuilder<T> implements UserAttributeDiffBuilder<T> {

  private final UserType userType;
  private final UserAttribute userAttribute;
  private final T newValue;

  AbstractUserAttributeDiffBuilder(UserType userType, UserAttribute userAttribute, T newValue) {
    this.userType = userType;
    this.userAttribute = userAttribute;
    this.newValue = newValue;
  }

  @Override
  public boolean doesDiffExist() {
    return newValue != null && !newValue.equals(getAttributeValue());
  }

  @Override
  public UserAttributeDiff<T> buildUserAttributeDiff() {
    UserAttributeDiff<T> userAttributeDiff = createUserAttributeDiff();
    userAttributeDiff.setNewValue(newValue);
    userAttributeDiff.setOldValue(getAttributeValue());
    userAttributeDiff.setAttributeType(buildAttributeType());
    return userAttributeDiff;
  }

  abstract UserAttributeDiff<T> createUserAttributeDiff();

  abstract T getAttributeValue();

  abstract AttributeType buildAttributeType();

  UserType getUserType() {
    return userType;
  }

  T getNewValue() {
    return newValue;
  }

   UserAttribute getUserAttribute() {
    return userAttribute;
  }
}
