package gov.ca.cwds.idm.service.diff;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public class CollectionUserAttributeDiff extends UserAttributeDiff<Set<String>> {

  public CollectionUserAttributeDiff(UserAttribute userAttribute,
      Set<String> oldValue, Set<String> newValue) {
    super(userAttribute, oldValue, newValue);
  }

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

  public static Set<String> getOldValue(UserType existingUser, UserAttribute userAttribute) {
    return CognitoUtils.getDelimitedAttributeValue(existingUser, userAttribute);
  }
}
