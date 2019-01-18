package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import java.util.Set;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class CollectionAttributeDiffBuilder extends AbstractUserAttributeDiffBuilder<Set<String>> {

  public CollectionAttributeDiffBuilder(UserType userType,
      UserAttribute userAttribute,
      Set<String> newValue) {
    super(userType, userAttribute, newValue);
  }

  @Override
  UserAttributeDiff<Set<String>> createUserAttributeDiff() {
    return new CollectionUserAttributeDiff();
  }

  @Override
  Set<String> getAttributeValue() {
    return CognitoUtils.getDelimitedAttributeValue(getUserType(), getUserAttribute());
  }

  @Override
  AttributeType buildAttributeType() {
    return CognitoUtils.createDelimitedAttribute(getUserAttribute(), getNewValue());
  }
}
