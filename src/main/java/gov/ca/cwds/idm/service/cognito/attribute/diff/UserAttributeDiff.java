package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import java.util.List;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public abstract class UserAttributeDiff<T> {

  private UserAttribute userAttribute;
  private UserType exitingUser;
  private T newValue;

  public UserAttributeDiff(UserAttribute userAttribute, UserType exitingUser, T newValue) {
    this.userAttribute = userAttribute;
    this.newValue = newValue;
    this.exitingUser = exitingUser;
  }

  public abstract List<AttributeType> createAttributeTypes();

  public abstract T getOldValue();

  public abstract String getOldValueAsString();

  public abstract String getNewValueAsString();

  public T getNewValue() {
    return newValue;
  }

  public UserAttribute getUserAttribute() {
    return userAttribute;
  }

  public void setNewValue(T newValue) {
    this.newValue = newValue;
  }

  UserType getExitingUser() {
    return exitingUser;
  }

}
