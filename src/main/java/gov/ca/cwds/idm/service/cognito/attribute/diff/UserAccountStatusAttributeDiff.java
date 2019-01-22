package gov.ca.cwds.idm.service.cognito.attribute.diff;

import static gov.ca.cwds.idm.service.cognito.attribute.OtherUserAttribute.ACCOUNT_STATUS;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import java.util.List;

/**
 * Created by Alexander Serbin on 1/21/2019
 */
public class UserAccountStatusAttributeDiff extends UserAttributeDiff<Boolean> {

  public UserAccountStatusAttributeDiff(UserType exitingUser, Boolean newValue) {
    super(ACCOUNT_STATUS, exitingUser, newValue);
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
