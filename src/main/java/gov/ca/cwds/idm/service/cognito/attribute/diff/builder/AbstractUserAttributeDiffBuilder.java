package gov.ca.cwds.idm.service.cognito.attribute.diff.builder;

import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
abstract class AbstractUserAttributeDiffBuilder<T> implements
    UserAttributeDiffBuilder<T> {

  private final UserAttribute userAttribute;
  private final T newValue;
  private final T oldValue;

  AbstractUserAttributeDiffBuilder(UserAttribute userAttribute, T oldValue, T newValue) {
    this.userAttribute = userAttribute;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public UserAttribute getUserAttribute() {
    return userAttribute;
  }

  public T getNewValue() {
    return newValue;
  }

  public T getOldValue() {
    return oldValue;
  }

  @Override
  public boolean doesDiffExist() {
    return newValue != null && !newValue.equals(oldValue);
  }
}
