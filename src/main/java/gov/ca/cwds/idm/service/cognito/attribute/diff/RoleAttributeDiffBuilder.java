package gov.ca.cwds.idm.service.cognito.attribute.diff;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import java.util.Set;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public class RoleAttributeDiffBuilder extends CollectionAttributeDiffBuilder {

  public RoleAttributeDiffBuilder(UserType userType,
      UserAttribute userAttribute,
      Set<String> newValue) {
    super(userType, userAttribute, newValue);
  }

  @Override
  UserAttributeDiff<Set<String>> createUserAttributeDiff() {
    return new RolesUserAttributeDiff();
  }

}
