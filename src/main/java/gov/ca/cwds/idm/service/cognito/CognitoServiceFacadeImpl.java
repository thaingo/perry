package gov.ca.cwds.idm.service.cognito;

import static gov.ca.cwds.idm.persistence.ns.OperationType.GET;
import static gov.ca.cwds.idm.persistence.ns.OperationType.RESEND_INVITATION_EMAIL;
import static gov.ca.cwds.idm.persistence.ns.OperationType.UPDATE;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.EMAIL_DELIVERY;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.buildCreateUserAttributes;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.getEmail;
import static gov.ca.cwds.service.messages.MessageCode.ERROR_AT_INVITATION_RESENDING_ON_EMAIL_CHANGE;
import static gov.ca.cwds.service.messages.MessageCode.ERROR_CONNECT_TO_IDM;
import static gov.ca.cwds.service.messages.MessageCode.ERROR_GET_USER_FROM_IDM;
import static gov.ca.cwds.service.messages.MessageCode.ERROR_UPDATE_USER_IN_IDM;
import static gov.ca.cwds.service.messages.MessageCode.IDM_ERROR_AT_RESEND_INVITATION_EMAIL;
import static gov.ca.cwds.service.messages.MessageCode.IDM_GENERIC_ERROR;
import static gov.ca.cwds.service.messages.MessageCode.IDM_USER_VALIDATION_FAILED;
import static gov.ca.cwds.service.messages.MessageCode.USER_NOT_FOUND_BY_ID_IN_IDM;
import static gov.ca.cwds.service.messages.MessageCode.USER_WITH_EMAIL_EXISTS_IN_IDM;
import static gov.ca.cwds.util.Utils.toLowerCase;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.AmazonWebServiceResult;
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
import com.amazonaws.services.cognitoidp.model.AdminResetUserPasswordRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import com.amazonaws.services.cognitoidp.model.DescribeUserPoolRequest;
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.MessageActionType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserEnableStatusRequest;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.service.cognito.attribute.UpdatedAttributesBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUserPage;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import liquibase.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service(value = "cognitoServiceFacade")
@Profile("idm")
@SuppressWarnings({"fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS"})
public class CognitoServiceFacadeImpl implements CognitoServiceFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(CognitoServiceFacadeImpl.class);

  private CognitoProperties properties;

  private AWSCognitoIdentityProvider identityProvider;

  private ExceptionFactory exceptionFactory;

  private MessagesService messagesService;

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

  /**
   * {@inheritDoc}
   */
  @Override
  public UserType createUser(User user) {

    AdminCreateUserRequest request = createAdminCreateUserRequest(user);

    try {
      AdminCreateUserResult result = identityProvider.adminCreateUser(request);
      return result.getUser();

    } catch (UsernameExistsException e) {
      throw exceptionFactory
          .createUserAlreadyExistsException(USER_WITH_EMAIL_EXISTS_IN_IDM, e, user.getEmail());
    } catch (InvalidParameterException e) {
      throw exceptionFactory.createValidationException(IDM_USER_VALIDATION_FAILED, e);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public AdminCreateUserRequest createAdminCreateUserRequest(User user) {

    user.setEmail(toLowerCase(user.getEmail()));

    return
        new AdminCreateUserRequest()
            .withUsername(user.getEmail())
            .withUserPoolId(properties.getUserpool())
            .withDesiredDeliveryMediums(EMAIL_DELIVERY)
            .withUserAttributes(buildCreateUserAttributes(user));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CognitoUserPage searchPage(CognitoUsersSearchCriteria searchCriteria) {
    ListUsersRequest request = composeListUsersRequest(searchCriteria);
    try {
      ListUsersResult result = identityProvider.listUsers(request);
      return new CognitoUserPage(result.getUsers(), result.getPaginationToken());
    } catch (Exception e) {
      throw exceptionFactory.createIdmException(ERROR_CONNECT_TO_IDM, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UserType> searchAllPages(CognitoUsersSearchCriteria searchCriteria) {
    List<UserType> result = new ArrayList<>();
    addPage(result, searchCriteria);
    return result;
  }

  private void addPage(List<UserType> result, CognitoUsersSearchCriteria searchCriteria) {
    CognitoUserPage userPage = searchPage(searchCriteria);
    result.addAll(userPage.getUsers());
    String paginationToken = userPage.getPaginationToken();

    if (StringUtils.isNotEmpty(paginationToken)) {
      CognitoUsersSearchCriteria searchCriteria2 = new CognitoUsersSearchCriteria(searchCriteria);
      searchCriteria2.setPaginationToken(paginationToken);
      addPage(result, searchCriteria2);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void healthCheck() {
    identityProvider.describeUserPool(
        new DescribeUserPoolRequest().withUserPoolId(properties.getUserpool()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserType getCognitoUserById(String id) {
    AdminGetUserRequest request = createAdminGetUserRequest(id);
    AdminGetUserResult agur;

    agur = executeUserOperationInCognito(identityProvider::adminGetUser, request, id, GET);

    return new UserType()
        .withUsername(agur.getUsername())
        .withAttributes(agur.getUserAttributes())
        .withEnabled(agur.getEnabled())
        .withUserCreateDate(agur.getUserCreateDate())
        .withUserLastModifiedDate(agur.getUserLastModifiedDate())
        .withUserStatus(agur.getUserStatus());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AdminGetUserRequest createAdminGetUserRequest(String id) {
    return new AdminGetUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean updateUserAttributes(
      String userId, UserType existedCognitoUser, UserUpdate updateUserDto) {

    Map<UserAttribute, AttributeType> updatedAttributes =
        new UpdatedAttributesBuilder(existedCognitoUser, updateUserDto).getUpdatedAttributes();

    if (updatedAttributes.isEmpty()) {
        return false;
    }

    AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest =
        new AdminUpdateUserAttributesRequest()
            .withUsername(userId)
            .withUserPoolId(properties.getUserpool())
            .withUserAttributes(updatedAttributes.values());

    try {
      identityProvider.adminUpdateUserAttributes(adminUpdateUserAttributesRequest);
    } catch (com.amazonaws.services.cognitoidp.model.UserNotFoundException e) {
      throw exceptionFactory.createUserNotFoundException(USER_NOT_FOUND_BY_ID_IN_IDM, e, userId);
    } catch (com.amazonaws.services.cognitoidp.model.AliasExistsException e) {
      throw exceptionFactory.createUserAlreadyExistsException(USER_WITH_EMAIL_EXISTS_IN_IDM, e,
          updateUserDto.getEmail());
    } catch (Exception e) {
      throw exceptionFactory.createIdmException(getErrorCode(UPDATE), e, userId);
    }

    if(updatedAttributes.containsKey(EMAIL)) {
      String newEmail = updatedAttributes.get(EMAIL).getValue();
      resendInvitationEmailOnEmailChange(userId, newEmail);
    }

    return true;
  }

  private void resendInvitationEmailOnEmailChange(String userId, String newEmail){
    try {
      identityProvider.adminResetUserPassword(createAdminResetUserPasswordRequest(newEmail));
    } catch (Exception e) {
      String msg = messagesService
          .getTechMessage(ERROR_AT_INVITATION_RESENDING_ON_EMAIL_CHANGE, userId);
      LOGGER.error(msg, e);
    }
  }

  @Override
  public AdminResetUserPasswordRequest createAdminResetUserPasswordRequest(String newEmail) {
    return new AdminResetUserPasswordRequest()
        .withUsername(newEmail)
        .withUserPoolId(properties.getUserpool());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean changeUserEnabledStatus(UserEnableStatusRequest request) {
    boolean executed = false;

    String id = request.getUserId();
    Boolean existedEnabled = request.getExistedEnabled();
    Boolean newEnabled = request.getNewEnabled();

    if (newEnabled != null && !newEnabled.equals(existedEnabled)) {
      if (newEnabled) {
        AdminEnableUserRequest adminEnableUserRequest =
            new AdminEnableUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
        executeUserOperationInCognito(identityProvider::adminEnableUser, adminEnableUserRequest, id,
            UPDATE);
        executed = true;

      } else {
        AdminDisableUserRequest adminDisableUserRequest =
            new AdminDisableUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
        executeUserOperationInCognito(identityProvider::adminDisableUser, adminDisableUserRequest,
            id, UPDATE);
        executed = true;
      }
    }
    return executed;
  }

  @Override
  public UserType resendInvitationMessage(String userId) {
    UserType cognitoUser = getCognitoUserById(userId);
    String email = getEmail(cognitoUser);
    AdminCreateUserRequest request = createResendEmailRequest(email);
    AdminCreateUserResult result =
        executeUserOperationInCognito(identityProvider::adminCreateUser, request, userId,
            RESEND_INVITATION_EMAIL);
    return result.getUser();
  }

  /**
   * Creates the request for resending email.
   *
   * @param email email address of the user.
   */
  public AdminCreateUserRequest createResendEmailRequest(String email) {
    return new AdminCreateUserRequest()
        .withUsername(toLowerCase(email))
        .withUserPoolId(properties.getUserpool())
        .withMessageAction(MessageActionType.RESEND)
        .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL);
  }

  public ListUsersRequest composeListUsersRequest(CognitoUsersSearchCriteria criteria) {
    ListUsersRequest request = new ListUsersRequest().withUserPoolId(properties.getUserpool());
    if (criteria.getPageSize() != null) {
      request = request.withLimit(criteria.getPageSize());
    }
    if (criteria.getPaginationToken() != null) {
      request = request.withPaginationToken(criteria.getPaginationToken());
    }
    if (criteria.getSearchAttrName() != null) {
      request = request.withFilter(
          criteria.getSearchAttrName() + " = \"" + criteria.getSearchAttrValue() + "\"");
    }
    return request;
  }

  private <T extends AmazonWebServiceRequest, R extends AmazonWebServiceResult>
  R executeUserOperationInCognito(Function<T, R> function, T request, String userId,
      OperationType operation) {

    try {
      return function.apply(request);
    } catch (com.amazonaws.services.cognitoidp.model.UserNotFoundException e) {
      throw exceptionFactory.createUserNotFoundException(USER_NOT_FOUND_BY_ID_IN_IDM, e, userId);
    } catch (Exception e) {
      throw exceptionFactory.createIdmException(getErrorCode(operation), e, userId);
    }
  }

  private MessageCode getErrorCode(OperationType operation) {
    if (operation == UPDATE) {
      return ERROR_UPDATE_USER_IN_IDM;
    } else if (operation == GET) {
      return ERROR_GET_USER_FROM_IDM;
    } else if (operation == RESEND_INVITATION_EMAIL) {
      return IDM_ERROR_AT_RESEND_INVITATION_EMAIL;
    } else {
      return IDM_GENERIC_ERROR;
    }
  }

  @Autowired
  public void setProperties(CognitoProperties properties) {
    this.properties = properties;
  }

  public AWSCognitoIdentityProvider getIdentityProvider() {
    return identityProvider;
  }

  public void setIdentityProvider(AWSCognitoIdentityProvider identityProvider) {
    this.identityProvider = identityProvider;
  }

  @Autowired
  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }

  @Autowired
  public void setMessagesService(MessagesService messagesService) {
    this.messagesService = messagesService;
  }
}
