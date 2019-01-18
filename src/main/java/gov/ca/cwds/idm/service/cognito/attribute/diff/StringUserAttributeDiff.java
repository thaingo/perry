package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public final class StringUserAttributeDiff extends UserAttributeDiff<String> {

  public StringUserAttributeDiff(AttributeType attributeType,
      String oldValue, String newValue) {
    super(attributeType, oldValue, newValue);
  }

  StringUserAttributeDiff() {
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
