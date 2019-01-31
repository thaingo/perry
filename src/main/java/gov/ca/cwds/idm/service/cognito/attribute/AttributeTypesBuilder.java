package gov.ca.cwds.idm.service.cognito.attribute;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.idm.service.diff.UserAttributeDiff;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeTypesBuilder {

  private final Map<UserAttribute, UserAttributeDiff> diffMap;
  private final List<AttributeType> attributeTypes;

  public AttributeTypesBuilder(Map<UserAttribute, UserAttributeDiff> diffMap) {
    this.diffMap = diffMap;
    this.attributeTypes = new ArrayList<>(diffMap.size());
  }

  public AttributeTypesBuilder addAttribute(UserAttribute userAttribute) {
    if (diffMap.containsKey(userAttribute)) {
      attributeTypes.addAll(diffMap.get(userAttribute).createAttributeTypes());
    }
    return this;
  }

  public List<AttributeType> build() {
    return attributeTypes;
  }
}
