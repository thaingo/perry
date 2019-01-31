package gov.ca.cwds.idm.service.cognito.attribute.diff;

import static gov.ca.cwds.idm.service.cognito.attribute.OtherUserAttribute.ENABLED_STATUS;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import java.util.List;

/**
 * Created by Alexander Serbin on 1/21/2019
 */
public class UserEnabledStatusAttributeDiff extends UserAttributeDiff<Boolean> {

  public UserEnabledStatusAttributeDiff(User exitingUser, Boolean oldValue, Boolean newValue) {
    super(ENABLED_STATUS, exitingUser, oldValue, newValue);
  }

  @Override
  public List<AttributeType> createAttributeTypes() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean getOldValue() {
    return getExitingUser().getEnabled();
  }

  @Override
  public String getOldValueAsString() {
    return String.valueOf(getOldValue());
  }

  @Override
  public String getNewValueAsString() {
    return getNewValue().toString();
  }

  public static Boolean getOldValue(UserType userType) {
    return userType.getEnabled();
  }

}
