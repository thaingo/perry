package gov.ca.cwds.idm.service.cognito.util;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.IS_LOCKED;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.MAX_LOGIN_ATTEMPTS;
import static gov.ca.cwds.idm.service.cognito.attribute.UserLockStatus.FALSE;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.buildCreateUserAttributes;
import static gov.ca.cwds.util.Utils.toLowerCase;

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import com.amazonaws.services.cognitoidp.model.DescribeUserPoolRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.MessageActionType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.CognitoProperties;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public final class CognitoRequestHelper {

  public static final String RESET_MAX_LOGIN_ATTEMPTS_COUNT = "0";
  private CognitoProperties properties;

  @Autowired
  public CognitoRequestHelper(CognitoProperties properties) {
    this.properties = properties;
  }

  public List<AttributeType> getLockedAttributeType() {
    final AttributeType isLockedAttributeType =
        new AttributeType()
            .withName(IS_LOCKED.getName())
            .withValue(FALSE.getValue());

    final AttributeType loginAttemptsAttributeType =
        new AttributeType()
            .withName(MAX_LOGIN_ATTEMPTS.getName())
            .withValue(RESET_MAX_LOGIN_ATTEMPTS_COUNT);

    return Arrays.asList(isLockedAttributeType, loginAttemptsAttributeType);
  }

  public AdminUpdateUserAttributesRequest getAdminUpdateUserAttributesRequest(
      String userId, List<AttributeType> attributeTypes) {
    return new AdminUpdateUserAttributesRequest()
        .withUsername(userId)
        .withUserPoolId(properties.getUserpool())
        .withUserAttributes(attributeTypes);
  }

  public AdminCreateUserRequest getAdminCreateUserRequest(User user) {
    user.setEmail(toLowerCase(user.getEmail()));
    return new AdminCreateUserRequest()
        .withUsername(user.getEmail())
        .withUserPoolId(properties.getUserpool())
        .withMessageAction(MessageActionType.SUPPRESS)
        .withUserAttributes(buildCreateUserAttributes(user));
  }

  public AdminDeleteUserRequest getAdminDeleteUserRequest(String id) {
    return new AdminDeleteUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
  }

  /**
   * Creates the request for resending email.
   *
   * @param email email address of the user.
   */
  public AdminCreateUserRequest getResendEmailRequest(String email) {
    return new AdminCreateUserRequest()
        .withUsername(toLowerCase(email))
        .withUserPoolId(properties.getUserpool())
        .withMessageAction(MessageActionType.RESEND)
        .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL);
  }

  public AdminGetUserRequest getAdminGetUserRequest(String id) {
    return new AdminGetUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
  }

  public ListUsersRequest composeListUsersRequest(CognitoUsersSearchCriteria criteria) {
    ListUsersRequest request = getListUsersRequest();
    if (criteria.getPageSize() != null) {
      request = request.withLimit(criteria.getPageSize());
    }
    if (criteria.getPaginationToken() != null) {
      request = request.withPaginationToken(criteria.getPaginationToken());
    }
    if (criteria.getSearchAttrName() != null) {
      request =
          request.withFilter(
              criteria.getSearchAttrName() + " = \"" + criteria.getSearchAttrValue() + "\"");
    }
    return request;
  }

  public ListUsersRequest getListUsersRequest() {
    return new ListUsersRequest().withUserPoolId(properties.getUserpool());
  }

  public AdminDisableUserRequest getAdminDisableUserRequest(String id) {
    return new AdminDisableUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
  }

  public AdminEnableUserRequest getAdminEnableUserRequest(String id) {
    return new AdminEnableUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
  }

  public DescribeUserPoolRequest getDescribeUserPoolRequest() {
    return new DescribeUserPoolRequest().withUserPoolId(properties.getUserpool());
  }
}
