package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public class CollectionUserAttributeDiff extends UserAttributeDiff<Set<String>> {

  public CollectionUserAttributeDiff(
      AttributeType attributeType,
      Set<String> oldValue, Set<String> newValue) {
    super(attributeType, oldValue, newValue);
  }

  CollectionUserAttributeDiff() {}

  @Override
  public String getOldValueAsString() {
    return StringUtils.join(getOldValue(), ", ");
  }

  @Override
  public String getNewValueAsString() {
    return StringUtils.join(getNewValue(), ", ");
  }
}
