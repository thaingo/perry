package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.TRUE_VALUE;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.createDelimitedAttribute;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.idm.service.diff.Diff;
import gov.ca.cwds.idm.service.diff.UserAttributeDiff;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AttributeTypesBuilder {

  private final Map<UserAttribute, UserAttributeDiff> diffMap;
  private final List<AttributeType> attributeTypes;

  public AttributeTypesBuilder(Map<UserAttribute, UserAttributeDiff> diffMap) {
    this.diffMap = diffMap;
    this.attributeTypes = new ArrayList<>(diffMap.size());
  }

  public List<AttributeType> build(UserAttribute... userAttributes) {
    for(UserAttribute userAttribute : userAttributes) {
      addAttribute(userAttribute);
    }
    return attributeTypes;
  }

  private void addAttribute(UserAttribute userAttribute) {
    if (!diffMap.containsKey(userAttribute)) {
      return;
    }

    Diff diff = diffMap.get(userAttribute);

    if(userAttribute == EMAIL) {
      addStringAttribute(EMAIL, (String)diff.getNewValue());
      addStringAttribute(EMAIL_VERIFIED, TRUE_VALUE);

    } else if(userAttribute == ROLES || userAttribute == PERMISSIONS) {
      attributeTypes.add(createDelimitedAttribute(userAttribute, (Set<String>)diff.getNewValue()));

    } else {
      addStringAttribute(userAttribute, (String)diff.getNewValue());
    }
  }

  private void addStringAttribute(UserAttribute userAttribute, String value) {
    attributeTypes.add(new AttributeType().withName(userAttribute.getName()).withValue(value));
  }
}
