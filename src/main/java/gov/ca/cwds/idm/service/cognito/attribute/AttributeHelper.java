package gov.ca.cwds.idm.service.cognito.attribute;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;

public interface AttributeHelper<T> {

  T getAttributeValue(UserType userType, UserAttribute userAttribute);

  AttributeType createAttribute(UserAttribute userAttribute, T value);
}
