package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public abstract class UserAttributeDiff<T> {

  private AttributeType attributeType;
  private T oldValue;
  private T newValue;

  public UserAttributeDiff(AttributeType attributeType, T oldValue, T newValue) {
    this.attributeType = attributeType;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  UserAttributeDiff() {}

  public AttributeType getAttributeType() {
    return attributeType;
  }

  public void setAttributeType(AttributeType attributeType) {
    this.attributeType = attributeType;
  }

  public T getOldValue() {
    return oldValue;
  }

  public void setOldValue(T oldValue) {
    this.oldValue = oldValue;
  }

  public T getNewValue() {
    return newValue;
  }

  public abstract String getOldValueAsString();
  public abstract String getNewValueAsString();

  public void setNewValue(T newValue) {
    this.newValue = newValue;
  }
}
