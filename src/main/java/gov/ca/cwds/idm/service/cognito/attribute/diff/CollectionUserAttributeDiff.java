package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import java.util.Set;
import java.util.TreeSet;
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
    return getCollectionValueAsString(getOldValue());
  }

  @Override
  public String getNewValueAsString() {
    return getCollectionValueAsString(getNewValue());
  }

  protected static String getCollectionValueAsString(Set<String> collection) {
    if (collection == null) {
      return "";
    } else {
      return StringUtils.join(new TreeSet<>(collection), ", ");
    }
  }
}
