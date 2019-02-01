package gov.ca.cwds.idm.service.diff;

import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public abstract class UserAttributeDiff<T> implements Diff<T> {

  private UserAttribute userAttribute;
  private T oldValue;
  private T newValue;

  public UserAttributeDiff(UserAttribute userAttribute, T oldValue, T newValue) {
    this.userAttribute = userAttribute;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  public T getNewValue() {
    return newValue;
  }

  @Override
  public T getOldValue() {
    return oldValue;
  }

  public UserAttribute getUserAttribute() {
    return userAttribute;
  }
}
