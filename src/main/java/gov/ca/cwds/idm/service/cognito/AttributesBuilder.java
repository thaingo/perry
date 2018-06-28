package gov.ca.cwds.idm.service.cognito;

import static com.google.common.base.Strings.nullToEmpty;
import static gov.ca.cwds.idm.service.cognito.CognitoUtils.attribute;

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

  public AttributesBuilder addAttribute(String attrName, String attrValue) {
    AttributeType attr = attribute(attrName, attrValue);
    return addAttribute(attr);
  }
}
