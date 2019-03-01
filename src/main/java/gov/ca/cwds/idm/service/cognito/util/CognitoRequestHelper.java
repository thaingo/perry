package gov.ca.cwds.idm.service.cognito.util;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.IS_LOCKED;
import static gov.ca.cwds.idm.service.cognito.attribute.UserLockStatus.FALSE;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.buildCreateUserAttributes;
import static gov.ca.cwds.util.Utils.toLowerCase;

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import com.amazonaws.services.cognitoidp.model.MessageActionType;
import gov.ca.cwds.idm.dto.User;
import java.util.Collections;
import java.util.List;

public final class CognitoRequestHelper {

  private CognitoRequestHelper() {}

  public static List<AttributeType> createLockedAttributeType() {
    return Collections.singletonList(
        new AttributeType().withName(IS_LOCKED.getName()).withValue(FALSE.getValue()));
  }

  public static AdminUpdateUserAttributesRequest createAdminUpdateUserAttributesRequest(
      String userId, String userPool, List<AttributeType> attributeTypes) {
    return new AdminUpdateUserAttributesRequest()
        .withUsername(userId)
        .withUserPoolId(userPool)
        .withUserAttributes(attributeTypes);
  }

  public static AdminCreateUserRequest createAdminCreateUserRequest(User user, String userPool) {
    user.setEmail(toLowerCase(user.getEmail()));
    return new AdminCreateUserRequest()
        .withUsername(user.getEmail())
        .withUserPoolId(userPool)
        .withMessageAction(MessageActionType.SUPPRESS)
        .withUserAttributes(buildCreateUserAttributes(user));
  }

  public static AdminDeleteUserRequest createAdminDeleteUserRequest(String id, String userPool) {
    return new AdminDeleteUserRequest().withUsername(id).withUserPoolId(userPool);
  }

  /**
   * Creates the request for resending email.
   *
   * @param email email address of the user.
   */
  public static AdminCreateUserRequest createResendEmailRequest(String email, String userPool) {
    return new AdminCreateUserRequest()
        .withUsername(toLowerCase(email))
        .withUserPoolId(userPool)
        .withMessageAction(MessageActionType.RESEND)
        .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL);
  }

  public static AdminGetUserRequest createAdminGetUserRequest(String id, String userPool) {
    return new AdminGetUserRequest().withUsername(id).withUserPoolId(userPool);
  }
}
