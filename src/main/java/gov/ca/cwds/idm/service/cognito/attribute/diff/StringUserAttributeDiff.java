package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public class StringUserAttributeDiff extends UserAttributeDiff<String> {

  public StringUserAttributeDiff(UserAttribute userAttribute,
      UserType existingUser, String newValue) {
    super(userAttribute, existingUser, newValue);
  }

  @Override
  public String getOldValue() {
    return getOldValue(getExitingUser(), getUserAttribute());
  }

  @Override
  public List<AttributeType> createAttributeTypes() {
    return  Collections.singletonList(
        new AttributeType().withName(getUserAttribute().getName()).withValue(getNewValue()));
  }

  @Override
  public String getOldValueAsString() {
    return getOldValue();
  }

  @Override
  public String getNewValueAsString() {
    return getNewValue();
  }

  public static String getOldValue(UserType userType, UserAttribute userAttribute) {
    return CognitoUtils.getAttributeValue(userType, userAttribute);
  }

}
