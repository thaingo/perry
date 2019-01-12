package gov.ca.cwds.idm.service.cognito.attribute;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import java.util.Set;

public class CollectionAttributeHelper implements AttributeHelper<Set<String>>{

  @Override
  public Set<String> getAttributeValue(UserType userType, UserAttribute userAttribute) {
    return CognitoUtils.getDelimitedAttributeValue(userType, userAttribute);
  }

  @Override
  public AttributeType createAttribute(UserAttribute userAttribute, Set<String> value) {
    return CognitoUtils.createDelimitedAttribute(userAttribute, value);
  }
}
