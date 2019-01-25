package gov.ca.cwds.idm.service.cognito.attribute.diff.builder;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
abstract class AbstractUserAttributeDiffBuilder<T> implements
    UserAttributeDiffBuilder<T> {

  private final UserType userType;
  private final UserAttribute userAttribute;
  private final T newValue;

  AbstractUserAttributeDiffBuilder(UserAttribute userAttribute, UserType userType, T newValue) {
    this.userType = userType;
    this.userAttribute = userAttribute;
    this.newValue = newValue;
  }

  public UserType getUserType() {
    return userType;
  }

  public UserAttribute getUserAttribute() {
    return userAttribute;
  }

  public T getNewValue() {
    return newValue;
  }

  static <T> boolean doesDiffExist(T newValue, T oldValue) {
    return newValue != null && !newValue.equals(oldValue);
  }

}
