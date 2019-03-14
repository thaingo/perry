package gov.ca.cwds.idm.service.cognito;

import static gov.ca.cwds.idm.persistence.ns.OperationType.GET;
import static gov.ca.cwds.idm.persistence.ns.OperationType.RESEND_INVITATION_EMAIL;
import static gov.ca.cwds.idm.persistence.ns.OperationType.UPDATE;
import static gov.ca.cwds.idm.service.cognito.attribute.UserUpdateAttributesUtil.buildUpdatedAttributesList;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.composeToGetFirstPageByEmail;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.composeToGetFirstPageByRacfId;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.getEmail;
import static gov.ca.cwds.service.messages.MessageCode.ERROR_CONNECT_TO_IDM;
import static gov.ca.cwds.service.messages.MessageCode.ERROR_GET_USER_FROM_IDM;
import static gov.ca.cwds.service.messages.MessageCode.ERROR_UPDATE_USER_IN_IDM;
import static gov.ca.cwds.service.messages.MessageCode.IDM_ERROR_AT_RESEND_INVITATION_EMAIL;
import static gov.ca.cwds.service.messages.MessageCode.IDM_GENERIC_ERROR;
import static gov.ca.cwds.service.messages.MessageCode.IDM_USER_VALIDATION_FAILED;
import static gov.ca.cwds.service.messages.MessageCode.USER_NOT_FOUND_BY_ID_IN_IDM;
import static gov.ca.cwds.service.messages.MessageCode.USER_WITH_EMAIL_EXISTS_IN_IDM;
import static gov.ca.cwds.util.Utils.toUpperCase;

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
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUserPage;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import gov.ca.cwds.idm.service.cognito.util.CognitoRequestHelper;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.service.messages.MessageCode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import liquibase.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service(value = "cognitoServiceFacade")
@Profile("idm")
@SuppressWarnings({"fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS"})
public class CognitoServiceFacadeImpl implements CognitoServiceFacade {

  private CognitoProperties properties;

  private AWSCognitoIdentityProvider identityProvider;

  private ExceptionFactory exceptionFactory;

  private CognitoRequestHelper cognitoRequestHelper;

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
  @SuppressWarnings({"fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CHECKED"})//no checked exceptions here
  public UserType createUser(User user) {

    AdminCreateUserRequest request = cognitoRequestHelper.getAdminCreateUserRequest(user);

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
  @SuppressWarnings({"fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CHECKED"})//no checked exceptions here
  public CognitoUserPage searchPage(CognitoUsersSearchCriteria searchCriteria) {
    ListUsersRequest request = cognitoRequestHelper.composeListUsersRequest(searchCriteria);
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
    identityProvider.describeUserPool(cognitoRequestHelper.getDescribeUserPoolRequest());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserType getCognitoUserById(String id) {
    AdminGetUserRequest request = cognitoRequestHelper.getAdminGetUserRequest(id);
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

  @Override
  public boolean isActiveRacfIdPresentInCognito(String racfId) {
    Collection<UserType> cognitoUsersByRacfId =
        searchAllPages(composeToGetFirstPageByRacfId(toUpperCase(racfId)));
    return !CollectionUtils.isEmpty(cognitoUsersByRacfId)
        && isActiveUserPresent(cognitoUsersByRacfId);
  }

  @Override
  public boolean doesUserWithEmailExistInCognito(String email) {
    Collection<UserType> cognitoUsers = searchPage(composeToGetFirstPageByEmail(email)).getUsers();
    return !CollectionUtils.isEmpty(cognitoUsers);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean updateUserAttributes(
      UserUpdateRequest userUpdateRequest) {

    UpdateDifference updateDifference = userUpdateRequest.getUpdateDifference();
    List<AttributeType> attributeTypes = buildUpdatedAttributesList(updateDifference);

    if (attributeTypes.isEmpty()) {
      return false;
    }

    AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest =
        cognitoRequestHelper.getAdminUpdateUserAttributesRequest(
            userUpdateRequest.getUserId(), attributeTypes);
    try {
      identityProvider.adminUpdateUserAttributes(adminUpdateUserAttributesRequest);
    } catch (com.amazonaws.services.cognitoidp.model.UserNotFoundException e) {
      throw exceptionFactory.createUserNotFoundException(USER_NOT_FOUND_BY_ID_IN_IDM, e,
          userUpdateRequest.getUserId());
    } catch (com.amazonaws.services.cognitoidp.model.AliasExistsException e) {
        final String newEmail = updateDifference.getEmailDiff().map(StringDiff::getNewValue).orElse("");
        throw exceptionFactory.createUserAlreadyExistsException(USER_WITH_EMAIL_EXISTS_IN_IDM, e,
                newEmail);
    } catch (Exception e) {
      throw exceptionFactory
          .createIdmException(getErrorCode(UPDATE), e, userUpdateRequest.getUserId());
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void changeUserEnabledStatus(String id, Boolean newValue) {
    if (newValue) {
      AdminEnableUserRequest adminEnableUserRequest =
          cognitoRequestHelper.getAdminEnableUserRequest(id);
      executeUserOperationInCognito(identityProvider::adminEnableUser, adminEnableUserRequest, id,
          UPDATE);
    } else {
      AdminDisableUserRequest adminDisableUserRequest =
          cognitoRequestHelper.getAdminDisableUserRequest(id);
      executeUserOperationInCognito(identityProvider::adminDisableUser, adminDisableUserRequest,
          id, UPDATE);
    }
  }

  @Override
  public UserType resendInvitationMessage(String userId) {
    UserType cognitoUser = getCognitoUserById(userId);
    String email = getEmail(cognitoUser);
    AdminCreateUserRequest request = cognitoRequestHelper.getResendEmailRequest(email);
    AdminCreateUserResult result =
        executeUserOperationInCognito(identityProvider::adminCreateUser, request, userId,
            RESEND_INVITATION_EMAIL);
    return result.getUser();
  }

  @Override
  public void unlockUser(String userId) {
    List<AttributeType> attributeTypes = cognitoRequestHelper.getLockedAttributeType();
    AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest =
        cognitoRequestHelper.getAdminUpdateUserAttributesRequest(userId, attributeTypes);
    executeUserOperationInCognito(
        identityProvider::adminUpdateUserAttributes,
        adminUpdateUserAttributesRequest,
        userId,
        UPDATE);
  }

  @Override
  public UserType sendInvitationMessageByEmail(String email) {
    AdminCreateUserRequest request = cognitoRequestHelper.getResendEmailRequest(email);
    return identityProvider.adminCreateUser(request).getUser();
  }

  @Override
  public void deleteCognitoUserById(String id) {
    AdminDeleteUserRequest request = cognitoRequestHelper.getAdminDeleteUserRequest(id);
    identityProvider.adminDeleteUser(request);
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

  private static boolean isActiveUserPresent(Collection<UserType> cognitoUsers) {
    return cognitoUsers
        .stream()
        .anyMatch(userType -> Objects.equals(userType.getEnabled(), Boolean.TRUE));
  }

  public AWSCognitoIdentityProvider getIdentityProvider() {
    return identityProvider;
  }

  public void setIdentityProvider(AWSCognitoIdentityProvider identityProvider) {
    this.identityProvider = identityProvider;
  }

  @Autowired
  public void setProperties(CognitoProperties properties) {
    this.properties = properties;
  }

  @Autowired
  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }

  @Autowired
  public void setCognitoRequestHelper(CognitoRequestHelper cognitoRequestHelper) {
    this.cognitoRequestHelper = cognitoRequestHelper;
  }

  public CognitoRequestHelper getCognitoRequestHelper() {
    return cognitoRequestHelper;
  }
}
