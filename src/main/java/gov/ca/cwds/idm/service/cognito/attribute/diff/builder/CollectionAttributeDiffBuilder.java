package gov.ca.cwds.idm.service.cognito.attribute.diff.builder;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.CollectionUserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import java.util.Set;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class CollectionAttributeDiffBuilder extends AbstractUserAttributeDiffBuilder<Set<String>> {

  public CollectionAttributeDiffBuilder(
      UserAttribute userAttribute, User user, Set<String> oldValue, Set<String> newValue) {
    super(userAttribute, user, oldValue, newValue);
  }

  @Override
  public UserAttributeDiff<Set<String>> buildDiff() {
    return new CollectionUserAttributeDiff(getUserAttribute(), getUser(), getOldValue(), getNewValue());
  }
}
