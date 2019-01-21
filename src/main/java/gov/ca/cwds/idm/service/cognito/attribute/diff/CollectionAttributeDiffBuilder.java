package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import java.util.Set;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class CollectionAttributeDiffBuilder extends AbstractUserAttributeDiffBuilder<Set<String>> {

  public CollectionAttributeDiffBuilder(
      UserAttribute userAttribute, UserType userType, Set<String> newValue) {
    super(userAttribute, userType, newValue);
  }

  @Override
  public boolean doesDiffExist() {
    return doesDiffExist(getNewValue(),
        CollectionUserAttributeDiff.getOldValue(getUserType(), getUserAttribute()));
  }

  @Override
  public UserAttributeDiff<Set<String>> buildDiff() {
    return new CollectionUserAttributeDiff(getUserAttribute(), getUserType(), getNewValue());
  }

}
