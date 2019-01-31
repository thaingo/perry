package gov.ca.cwds.idm.service.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public class StringUserAttributeDiff extends UserAttributeDiff<String> {

  public StringUserAttributeDiff(UserAttribute userAttribute, String oldValue, String newValue) {
    super(userAttribute, oldValue, newValue);
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
}
