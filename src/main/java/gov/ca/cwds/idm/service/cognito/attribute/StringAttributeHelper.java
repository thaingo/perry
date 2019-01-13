package gov.ca.cwds.idm.service.cognito.attribute;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;

public class StringAttributeHelper implements AttributeHelper<String> {

  @Override
  public String getAttributeValue(UserType userType, UserAttribute userAttribute) {
    return CognitoUtils.getAttributeValue(userType, userAttribute);
  }

  @Override
  public AttributeType createAttribute(UserAttribute userAttribute, String value) {
    return CognitoUtils.attribute(userAttribute, value);
  }
}
