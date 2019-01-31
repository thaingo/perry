package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import java.util.List;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public abstract class UserAttributeDiff<T> implements Diff<T> {

  private UserAttribute userAttribute;
  private User exitingUser;
  private T oldValue;
  private T newValue;

  public UserAttributeDiff(UserAttribute userAttribute, User exitingUser, T oldValue, T newValue) {
    this.userAttribute = userAttribute;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.exitingUser = exitingUser;
  }

  public abstract List<AttributeType> createAttributeTypes();

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

  public void setNewValue(T newValue) {
    this.newValue = newValue;
  }

  User getExitingUser() {
    return exitingUser;
  }

}
