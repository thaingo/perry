package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class StringAttributeDiffBuilder extends AbstractUserAttributeDiffBuilder<String> {

  public StringAttributeDiffBuilder(UserType userType,
      UserAttribute userAttribute,
      String newValue) {
    super(userType, userAttribute, newValue);
  }

  @Override
  UserAttributeDiff<String> createUserAttributeDiff() {
    return new StringUserAttributeDiff();
  }

  @Override
  String getAttributeValue() {
    return CognitoUtils.getAttributeValue(getUserType(), getUserAttribute());
  }

  @Override
  AttributeType buildAttributeType() {
    return new AttributeType().withName(getUserAttribute().getName()).withValue(getNewValue());
  }
}
