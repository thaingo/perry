package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.CognitoUtils.attribute;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class AttributesBuilder {

  private List<AttributeType> attrs = new ArrayList<>();

  public List<AttributeType> build() {
    return attrs;
  }

  public AttributesBuilder addAttribute(String attrName, String attrValue) {
    if (StringUtils.isNotEmpty(attrValue)) {
      attrs.add(attribute(attrName, attrValue));
    }
    return this;
  }

  public AttributesBuilder addAttribute(AttributeType attr) {
    if (StringUtils.isNotEmpty(attr.getValue())) {
      attrs.add(attr);
    }
    return this;
  }
}
