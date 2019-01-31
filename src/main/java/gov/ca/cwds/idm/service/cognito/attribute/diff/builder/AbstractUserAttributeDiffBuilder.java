package gov.ca.cwds.idm.service.cognito.attribute.diff.builder;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
abstract class AbstractUserAttributeDiffBuilder<T> implements
    UserAttributeDiffBuilder<T> {

  private final User user;
  private final UserAttribute userAttribute;
  private final T newValue;
  private final T oldValue;

  AbstractUserAttributeDiffBuilder(UserAttribute userAttribute, User user, T oldValue, T newValue) {
    this.user = user;
    this.userAttribute = userAttribute;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public User getUser() {
    return user;
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
