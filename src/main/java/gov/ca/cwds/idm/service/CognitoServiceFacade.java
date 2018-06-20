package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.CognitoUtils.COUNTY_ATTR_NAME;
import static gov.ca.cwds.idm.service.CognitoUtils.EMAIL_ATTR_NAME;
import static gov.ca.cwds.idm.service.CognitoUtils.FIRST_NAME_ATTR_NAME;
import static gov.ca.cwds.idm.service.CognitoUtils.LAST_NAME_ATTR_NAME;
import static gov.ca.cwds.idm.service.CognitoUtils.OFFICE_ATTR_NAME;
import static gov.ca.cwds.idm.service.CognitoUtils.PHONE_NUMBER_ATTR_NAME;
import static gov.ca.cwds.idm.service.CognitoUtils.RACFID_ATTR_NAME;
import static gov.ca.cwds.idm.service.CognitoUtils.ROLE_ATTR_NAME;
import static gov.ca.cwds.idm.service.CognitoUtils.COUNTY_ATTR_NAME_2;
import static gov.ca.cwds.idm.service.CognitoUtils.RACFID_ATTR_NAME_2;
import static gov.ca.cwds.idm.service.CognitoUtils.createPermissionsAttribute;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.DescribeUserPoolRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import gov.ca.cwds.idm.CognitoProperties;
import gov.ca.cwds.idm.dto.UpdateUserDto;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UsersSearchParameter;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.rest.api.domain.UserAlreadyExistsException;
import gov.ca.cwds.rest.api.domain.UserNotFoundPerryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service(value = "cognitoServiceFacade")
@Profile("idm")
public class CognitoServiceFacade {
  private static final Logger LOGGER = LoggerFactory.getLogger(CognitoServiceFacade.class);

  private static final String EMAIL_DELIVERY = "EMAIL";
  private static final String DEFAULT_ROLES = "CWS-worker";

  @Autowired private CognitoProperties properties;

  private AWSCognitoIdentityProvider identityProvider;

  @PostConstruct
  public void init() {
    AWSCredentialsProvider credentialsProvider =
        new AWSStaticCredentialsProvider(
            new BasicAWSCredentials(properties.getIamAccessKeyId(), properties.getIamSecretKey()));
    identityProvider =
        AWSCognitoIdentityProviderClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion(Regions.fromName(properties.getRegion()))
            .build();
  }

  public UserType getById(String id) {
    try {
      return getCognitoUserById(id);
    } catch (UserNotFoundException e) {
      throw new UserNotFoundPerryException("User with id=" + id + " is not found", e);
    } catch (Exception e) {
      throw new PerryException("Exception while getting user from AWS Cognito", e);
    }
  }

  public void updateUser(String id, UpdateUserDto updateUserDto) {
    try {
      UserType existedCognitoUser = getCognitoUserById(id);
      updateUserAttributes(id, existedCognitoUser, updateUserDto);
      changeUserEnabledStatus(id, existedCognitoUser.getEnabled(), updateUserDto.getEnabled());
    } catch (UserNotFoundException e) {
      throw new UserNotFoundPerryException("User with id=" + id + " is not found", e);
    } catch (Exception e) {
      throw new PerryException("Exception while updating user in AWS Cognito", e);
    }
  }

  public String createUser(User user) {

    final String email = user.getEmail();

    AdminCreateUserRequest request =
        new AdminCreateUserRequest()
            .withUsername(email)
            .withUserPoolId(properties.getUserpool())
            .withDesiredDeliveryMediums(EMAIL_DELIVERY)
            .withUserAttributes(buildCreateUserAttributes(user));

    AdminCreateUserResult result;

    try {
      result = identityProvider.adminCreateUser(request);
    } catch (UsernameExistsException e) {
      String msg =
          "Unable to create new Cognito user. A User with email " + email + " already exists.";
      LOGGER.error(msg);
      throw new UserAlreadyExistsException(msg, e);
    }

    return result.getUser().getUsername();
  }

  public String getCountyName(String userId) {
    try {
      return CognitoUtils.getCountyName(getCognitoUserById(userId));
    } catch (UserNotFoundException e) {
      throw new UserNotFoundPerryException("User with id=" + userId + " is not found", e);
    }
  }

  public Collection<UserType> search(UsersSearchParameter parameter) {
    ListUsersRequest request = composeRequest(parameter);
    try {
      ListUsersResult result = identityProvider.listUsers(request);
      return result.getUsers();
    } catch (Exception e) {
      throw new PerryException("Exception while connecting to AWS Cognito", e);
    }
  }

  public void healthCheck() {
    identityProvider.describeUserPool(
        new DescribeUserPoolRequest().withUserPoolId(properties.getUserpool()));
  }

  private List<AttributeType> buildCreateUserAttributes(User user) {
    AttributesBuilder attributesBuilder = new AttributesBuilder()
        .addAttribute(EMAIL_ATTR_NAME, user.getEmail())
        .addAttribute(FIRST_NAME_ATTR_NAME, user.getFirstName())
        .addAttribute(LAST_NAME_ATTR_NAME, user.getLastName())
        .addAttribute(OFFICE_ATTR_NAME, user.getOffice())
        .addAttribute(PHONE_NUMBER_ATTR_NAME, user.getPhoneNumber())
        .addAttribute(ROLE_ATTR_NAME, DEFAULT_ROLES)
        .addAttribute(COUNTY_ATTR_NAME, user.getCountyName())
        .addAttribute(COUNTY_ATTR_NAME_2, user.getCountyName())
        .addAttribute(RACFID_ATTR_NAME, user.getRacfid())
        .addAttribute(RACFID_ATTR_NAME_2, user.getRacfid())
        .addAttribute(createPermissionsAttribute(user.getPermissions()));
    return attributesBuilder.build();
  }

  private UserType getCognitoUserById(String id) {
    AdminGetUserRequest request =
        new AdminGetUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
    AdminGetUserResult agur = identityProvider.adminGetUser(request);
    return new UserType()
        .withUsername(agur.getUsername())
        .withAttributes(agur.getUserAttributes())
        .withEnabled(agur.getEnabled())
        .withUserCreateDate(agur.getUserCreateDate())
        .withUserLastModifiedDate(agur.getUserLastModifiedDate())
        .withUserStatus(agur.getUserStatus());
  }

  private void updateUserAttributes(
      String id, UserType existedCognitoUser, UpdateUserDto updateUserDto) {

    List<AttributeType> updateAttributes = getUpdateAttributes(existedCognitoUser, updateUserDto);

    if (!updateAttributes.isEmpty()) {
      AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest =
          new AdminUpdateUserAttributesRequest()
              .withUsername(id)
              .withUserPoolId(properties.getUserpool())
              .withUserAttributes(updateAttributes);

      identityProvider.adminUpdateUserAttributes(adminUpdateUserAttributesRequest);
    }
  }

  private List<AttributeType> getUpdateAttributes(
      UserType existedCognitoUser, UpdateUserDto updateUserDto) {
    List<AttributeType> updateAttributes = new ArrayList<>();

    Set<String> existedUserPermissions = CognitoUtils.getPermissions(existedCognitoUser);
    Set<String> newUserPermissions = updateUserDto.getPermissions();

    if (!existedUserPermissions.equals(newUserPermissions)) {
      AttributeType permissionsAttr = createPermissionsAttribute(newUserPermissions);
      updateAttributes.add(permissionsAttr);
    }

    return updateAttributes;
  }

  private void changeUserEnabledStatus(String id, Boolean existedEnabled, Boolean newEnabled) {
    if (newEnabled != null && !newEnabled.equals(existedEnabled)) {
      if (newEnabled) {
        AdminEnableUserRequest adminEnableUserRequest =
            new AdminEnableUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
        identityProvider.adminEnableUser(adminEnableUserRequest);
      } else {
        AdminDisableUserRequest adminDisableUserRequest =
            new AdminDisableUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
        identityProvider.adminDisableUser(adminDisableUserRequest);
      }
    }
  }

  private ListUsersRequest composeRequest(UsersSearchParameter parameter) {
    ListUsersRequest request = new ListUsersRequest().withUserPoolId(properties.getUserpool());
    if (parameter.getPageSize() != null) {
      request = request.withLimit(parameter.getPageSize());
    }
    if (parameter.getUserCounty() != null) {
      request = request.withFilter("preferred_username = \"" + parameter.getUserCounty() + "\"");
    }
    if (parameter.getLastName() != null) {
      request = request.withFilter("family_name ^= \"" + parameter.getLastName() + "\"");
    }
    return request;
  }

  public CognitoProperties getProperties() {
    return properties;
  }

  public void setProperties(CognitoProperties properties) {
    this.properties = properties;
  }

  public AWSCognitoIdentityProvider getIdentityProvider() {
    return identityProvider;
  }

  public void setIdentityProvider(AWSCognitoIdentityProvider identityProvider) {
    this.identityProvider = identityProvider;
  }
}
