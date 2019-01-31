package gov.ca.cwds.idm.service.diff;

import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.TRUE_VALUE;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public final class EmailUserAttributeDiff extends StringUserAttributeDiff {

  public EmailUserAttributeDiff(String oldValue, String newValue) {
    super(EMAIL, oldValue, newValue);
  }

  @Override
  public List<AttributeType> createAttributeTypes() {
    AttributeType attributeType =
        new AttributeType().withName(EMAIL_VERIFIED.getName()).withValue(TRUE_VALUE);
    List<AttributeType> attributeTypes = new ArrayList<>();
    attributeTypes.addAll(super.createAttributeTypes());
    attributeTypes.add(attributeType);
    return attributeTypes;
  }

}
