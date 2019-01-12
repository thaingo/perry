package gov.ca.cwds.idm.service.cognito.attribute;

import static com.google.common.base.Strings.nullToEmpty;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.attribute;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class AttributesBuilder {

  private List<AttributeType> attrs = new ArrayList<>();

  public List<AttributeType> build() {
    return attrs;
  }

  public AttributesBuilder addAttribute(AttributeType attr) {

    if (attr == null) {
      throw new IllegalArgumentException("Attribute may not be null");
    }

    if (StringUtils.isBlank(attr.getName())) {
      throw new IllegalArgumentException("Attribute name may not be empty");
    }

    String attrValue = nullToEmpty(attr.getValue()).trim();
    attr.setValue(attrValue);
    attrs.add(attr);

    return this;
  }

  public AttributesBuilder addAttribute(UserAttribute userAttribute, String attrValue) {
    if (userAttribute == null) {
      throw new IllegalArgumentException("UserAttribute may not be null");
    }
    AttributeType attr = attribute(userAttribute.getName(), attrValue);
    return addAttribute(attr);
  }
}
