package gov.ca.cwds.idm.service.diff.builder;

import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.diff.CollectionUserAttributeDiff;
import gov.ca.cwds.idm.service.diff.UserAttributeDiff;
import java.util.Set;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class CollectionAttributeDiffBuilder extends AbstractUserAttributeDiffBuilder<Set<String>> {

  public CollectionAttributeDiffBuilder(
      UserAttribute userAttribute, Set<String> oldValue, Set<String> newValue) {
    super(userAttribute, oldValue, newValue);
  }

  @Override
  public UserAttributeDiff<Set<String>> buildDiff() {
    return new CollectionUserAttributeDiff(getUserAttribute(), getOldValue(), getNewValue());
  }
}
