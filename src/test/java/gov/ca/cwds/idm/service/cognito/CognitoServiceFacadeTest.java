package gov.ca.cwds.idm.service.cognito;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.COUNTY;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.IS_LOCKED;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.MAX_LOGIN_ATTEMPTS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.OFFICE;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.FIRST_NAME;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.LAST_NAME;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.RACFID_STANDARD;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USERPOOL;
import static gov.ca.cwds.idm.util.TestHelper.getTestCognitoProperties;
import static gov.ca.cwds.idm.util.TestUtils.attr;
import static gov.ca.cwds.service.messages.MessageCode.USER_WITH_EMAIL_EXISTS_IN_IDM;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.exception.IdmException;

import gov.ca.cwds.idm.exception.UserNotFoundException;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import gov.ca.cwds.idm.service.cognito.util.CognitoRequestHelper;

import gov.ca.cwds.idm.service.diff.UpdateDifference;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.service.messages.Messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hamcrest.junit.ExpectedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

public class CognitoServiceFacadeTest {

  private CognitoServiceFacadeImpl facade;

  private AWSCognitoIdentityProvider identityProvider = mock(AWSCognitoIdentityProvider.class);

  private MessagesService messagesService = mock(MessagesService.class);

  @Before
  public void before() {
    final CognitoProperties properties = getTestCognitoProperties();

    facade = new CognitoServiceFacadeImpl();
    ExceptionFactory exceptionFactory = new ExceptionFactory();
    exceptionFactory.setMessagesService(messagesService);

    facade.setProperties(properties);
    facade.setIdentityProvider(identityProvider);
    facade.setExceptionFactory(exceptionFactory);
    facade.setCognitoRequestHelper(new CognitoRequestHelper(properties));

    when(messagesService.getMessages(any(MessageCode.class), ArgumentMatchers.<String>any()))
        .thenAnswer(i -> new Messages(i.getArgument(0),"", ""));
//        .thenReturn(new Messages("", ""));
  }

  @Test
  public void testGetById() {
    AdminGetUserResult mockResult = new AdminGetUserResult();
    mockResult.setUsername("id");
    mockResult.setEnabled(Boolean.TRUE);
    mockResult.setUserStatus("userstatus");

    List<AttributeType> mockAttrs = new ArrayList<>();
    AttributeType mockAttr = new AttributeType();
    mockAttr.setName("attrName");
    mockAttr.setValue("attrValue");
    mockAttrs.add(mockAttr);
    mockResult.setUserAttributes(mockAttrs);

    mockResult.setUserCreateDate(new Date(1000));
    mockResult.setUserLastModifiedDate(new Date(2000));

    when(identityProvider.adminGetUser(any(AdminGetUserRequest.class)))
        .thenReturn(mockResult);

    UserType UserType = facade.getCognitoUserById("id");
    AdminGetUserRequest expectedRequest = new AdminGetUserRequest().withUsername("id")
        .withUserPoolId(USERPOOL);
    verify(identityProvider, times(1)).adminGetUser(expectedRequest);
    assertThat(UserType.getUsername(), is("id"));
    assertThat(UserType.getEnabled(), is(Boolean.TRUE));
    assertThat(UserType.getUserStatus(), is("userstatus"));
    assertThat(UserType.getUserCreateDate(), is(new Date(1000)));
    assertThat(UserType.getUserLastModifiedDate(), is(new Date(2000)));

    List<AttributeType> attrs = UserType.getAttributes();
    assertThat(attrs, is(notNullValue()));
    assertThat(attrs, hasSize(1));
    AttributeType attr = attrs.get(0);
    assertThat(attr.getName(), is("attrName"));
    assertThat(attr.getValue(), is("attrValue"));
  }

  @Test(expected = UserNotFoundException.class)
  public void testGetByIdUserNotFoundException() {
    when(identityProvider.adminGetUser(any(AdminGetUserRequest.class)))
        .thenThrow(new com.amazonaws.services.cognitoidp.model.UserNotFoundException("user not found"));
    facade.getCognitoUserById("id");
  }

  @Test(expected = IdmException.class)
  public void testGetByIdException() {
    when(identityProvider.adminGetUser(any(AdminGetUserRequest.class)))
        .thenThrow(new RuntimeException());
    facade.getCognitoUserById("id");
  }

  @Test
  public void testCreateAdminCreateUserRequest() {
    User user = user();
    user.setEmail("GONZALES@Gmail.com");
    user.setPhoneNumber("1234567890");
    user.setPhoneExtensionNumber("54321");

    AdminCreateUserRequest request = getCognitoRequestHelper().getAdminCreateUserRequest(user);

    assertThat(request.getUsername(), is("gonzales@gmail.com"));

    assertThat(request.getUserPoolId(), is(USERPOOL));
    assertThat(request.getMessageAction(), is("SUPPRESS"));

    List<AttributeType> attrs = request.getUserAttributes();
    assertThat(attrs.isEmpty(), is(false));
    assertThat(attrs.size(), is(7));

    Map<String, String> attrMap = attrMap(attrs);

    assertAttr(attrMap, EMAIL, "gonzales@gmail.com");
    assertAttr(attrMap, FIRST_NAME, "Garcia");
    assertAttr(attrMap, LAST_NAME, "Gonzales");
    assertAttr(attrMap, COUNTY, "Madera");
    assertAttr(attrMap, OFFICE, "Office07IJ");
    assertAttr(attrMap, RACFID_STANDARD, "RUBBLBA");
    assertAttr(attrMap, EMAIL_VERIFIED, "True");
  }

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void testUpdateUserAttributesWithDuplicateEmail() {
    final String new_email = "newEmail@gmail.com";
    final String err_msg = "ERR_MSG";
    when(messagesService.getMessages(USER_WITH_EMAIL_EXISTS_IN_IDM, new_email.toLowerCase()))
            .thenReturn(new Messages(USER_WITH_EMAIL_EXISTS_IN_IDM, err_msg, err_msg));

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail(new_email);

    final UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
    userUpdateRequest.setExistedUser(user());
    userUpdateRequest.setUpdateDifference(new UpdateDifference(user(), userUpdate));

    when(identityProvider.adminUpdateUserAttributes(any(AdminUpdateUserAttributesRequest.class)))
      .thenThrow(new com.amazonaws.services.cognitoidp.model.AliasExistsException("user exists"));

    exception.expectMessage(err_msg);
    facade.updateUserAttributes(userUpdateRequest);
  }

  @Test
  public void testRacifIdLowerCase() {
    User user = user();
    user.setRacfid("rubblba ");

    AdminCreateUserRequest request = getCognitoRequestHelper().getAdminCreateUserRequest(user);
    Map<String, String> attrMap = attrMap(request.getUserAttributes());
    assertAttr(attrMap, RACFID_STANDARD, "RUBBLBA");
  }

  @Test
  public void testSearchAllPages() {

    UserType userType0 = userType("user0");
    UserType userType1 = userType("user1");
    UserType userType2 = userType("user2");
    UserType userType3 = userType("user3");
    UserType userType4 = userType("user4");

    CognitoUsersSearchCriteria searchCriteria = new CognitoUsersSearchCriteria();
    searchCriteria.setSearchAttr(EMAIL, "searchPage@all.email");
    searchCriteria.setPageSize(2);

    ListUsersRequest request0 =
        setListUsersRequestAndResponse(searchCriteria, null, "1", userType0, userType1);

    ListUsersRequest request1 =
        setListUsersRequestAndResponse(searchCriteria, "1", "2", userType2, userType3);

    ListUsersRequest request2 =
        setListUsersRequestAndResponse(searchCriteria, "2", null, userType4);

    List<UserType> userTypes = facade.searchAllPages(searchCriteria);

    verify(identityProvider, times(1)).listUsers(request0);
    verify(identityProvider, times(1)).listUsers(request1);
    verify(identityProvider, times(1)).listUsers(request2);

    InOrder inOrder = inOrder(identityProvider);
    inOrder.verify(identityProvider).listUsers(request0);
    inOrder.verify(identityProvider).listUsers(request1);
    inOrder.verify(identityProvider).listUsers(request2);

    assertThat(userTypes, hasSize(5));
    assertThat(userTypes.get(0).getUsername(), is("user0"));
    assertThat(userTypes.get(1).getUsername(), is("user1"));
    assertThat(userTypes.get(2).getUsername(), is("user2"));
    assertThat(userTypes.get(3).getUsername(), is("user3"));
    assertThat(userTypes.get(4).getUsername(), is("user4"));
  }

  @Test
  public void testComposeListUsersRequest(){
    CognitoUsersSearchCriteria criteria = new CognitoUsersSearchCriteria();
    criteria.setSearchAttr(RACFID_STANDARD, "ABC");
    ListUsersRequest request = getCognitoRequestHelper().composeListUsersRequest(criteria);
    assertThat(request.getFilter(), is(RACFID_STANDARD.getName() + " = \"ABC\""));
  }

  @Test
  public void testCreateAdminResendInvitationMessage() {
    String userId = "amzon-id-user-1";
    String userEmail = "user@email";

    AdminGetUserRequest expectedGetUserRequest = getCognitoRequestHelper().getAdminGetUserRequest(userId);
    AdminGetUserResult mockGetUserResult = new AdminGetUserResult();
    mockGetUserResult.setUsername(userId);
    Collection<AttributeType> attrs = new ArrayList<>();
    attrs.add(attr(EMAIL, userEmail));
    mockGetUserResult.withUserAttributes(attrs);
    when(identityProvider.adminGetUser(expectedGetUserRequest)).thenReturn(mockGetUserResult);

    AdminCreateUserRequest expectedRequest = getCognitoRequestHelper().getResendEmailRequest(userEmail);
    AdminCreateUserResult mockResult = new AdminCreateUserResult();
    UserType userType = userType(userEmail);
    mockResult.setUser(userType);
    when(identityProvider.adminCreateUser(expectedRequest)).thenReturn(mockResult);

    UserType UserType = facade.resendInvitationMessage(userId);
    verify(identityProvider, times(1)).adminCreateUser(expectedRequest);
    assertThat(UserType.getUsername(), is(userEmail));
  }

  @Test
  public void testCreateAdminDeleteUserRequest() {
    final String USER_ID = "user-id";

    AdminDeleteUserRequest request = getCognitoRequestHelper().getAdminDeleteUserRequest(USER_ID);

    assertThat(request, is(notNullValue()));
    assertThat(request.getUsername(), is(USER_ID));
    assertThat(request.getUserPoolId(), is("userpool"));
  }

  @Test
  public void testDeleteCognitoUserById() {
    final String USER_ID = "user-id";
    AdminDeleteUserRequest expectedRequest = getCognitoRequestHelper().getAdminDeleteUserRequest(USER_ID);

    facade.deleteCognitoUserById(USER_ID);

    verify(identityProvider, times(1)).adminDeleteUser(expectedRequest);
  }

  @Test
  public void testCreateUserUnlockUpdateRequest() {
    final String USER_ID = "user-id";

    AdminUpdateUserAttributesRequest request =
        getCognitoRequestHelper().getAdminUpdateUserAttributesRequest(USER_ID, getCognitoRequestHelper().getLockedAttributeType());

    assertThat(request, is(notNullValue()));
    assertThat(request.getUsername(), is(USER_ID));
    assertThat(request.getUserPoolId(), is(USERPOOL));
    assertThat(request.getUserAttributes(), is(getCognitoRequestHelper().getLockedAttributeType()));
    final Optional<AttributeType> isLockedAttributeType =
        request.getUserAttributes().stream()
            .filter(attributeType -> Objects.equals(attributeType.getName(), IS_LOCKED.getName()))
            .findAny();
    assertTrue(isLockedAttributeType.isPresent());
    assertThat(isLockedAttributeType.get().getValue(), is("false"));

    final Optional<AttributeType> loginAttemptsAttributeType =
        request.getUserAttributes().stream()
            .filter(
                attributeType ->
                    Objects.equals(attributeType.getName(), MAX_LOGIN_ATTEMPTS.getName()))
            .findAny();
    assertTrue(loginAttemptsAttributeType.isPresent());
    assertThat(loginAttemptsAttributeType.get().getValue(), is("0"));
  }

  @Test
  public void testUnlockUserById() {
    final String USER_ID = "user-id";
    AdminUpdateUserAttributesRequest expectedRequest =
        getCognitoRequestHelper().getAdminUpdateUserAttributesRequest(USER_ID, getCognitoRequestHelper().getLockedAttributeType());

    facade.unlockUser(USER_ID);

    verify(identityProvider, times(1)).adminUpdateUserAttributes(expectedRequest);
  }

  @Test
  public void testCreateResendEmailRequest() {
    final String USER_EMAIL = "USER@EMAIL.com";

    AdminCreateUserRequest request = getCognitoRequestHelper().getResendEmailRequest(USER_EMAIL);

    assertThat(request, is(notNullValue()));
    assertThat(request.getUsername(), is("user@email.com"));
    assertThat(request.getUserPoolId(), is(USERPOOL));
    assertThat(request.getMessageAction(), is("RESEND"));
    assertThat(request.getDesiredDeliveryMediums(), is(Collections.singletonList("EMAIL")));
  }

  @Test
  public void testSendInvitationMessageByEmail() {
    final String USER_EMAIL = "user@email.com";
    AdminCreateUserRequest expectedRequest = getCognitoRequestHelper().getResendEmailRequest(USER_EMAIL);
    when(identityProvider.adminCreateUser(expectedRequest)).thenReturn(new AdminCreateUserResult());

    facade.sendInvitationMessageByEmail(USER_EMAIL);

    verify(identityProvider, times(1)).adminCreateUser(expectedRequest);
  }

  private ListUsersRequest setListUsersRequestAndResponse(
      CognitoUsersSearchCriteria searchCriteria, String requestPaginationToken,
      String responsePaginationToken, UserType... userTypes) {

    CognitoUsersSearchCriteria searchCriteria1 = new CognitoUsersSearchCriteria(searchCriteria);
    searchCriteria1.setPaginationToken(requestPaginationToken);
    ListUsersRequest request = getCognitoRequestHelper().composeListUsersRequest(searchCriteria1);
    ListUsersResult listUsersResult =
        new ListUsersResult().withUsers(userTypes).withPaginationToken(responsePaginationToken);
    when(identityProvider.listUsers(request)).thenReturn(listUsersResult);
    return request;
  }

  private User user() {
    User user = new User();
    user.setEmail("gonzales@gmail.com");
    user.setFirstName("Garcia");
    user.setLastName("Gonzales");
    user.setCountyName("Madera");
    user.setRacfid("RUBBLBA ");
    user.setOfficeId("Office07IJ");
    user.setOfficePhoneNumber("+19161111111");
    user.setPermissions(toSet("RFA-rollout", "Hotline-rollout"));
    user.setRoles(toSet(COUNTY_ADMIN, CWS_WORKER));
    return user;
  }

  private UserType userType(String name) {
    UserType userType = new UserType();
    userType.setUsername(name);
    return userType;
  }

  private static Map<String, String> attrMap(List<AttributeType> attrs) {
    return attrs.stream().collect(Collectors.toMap(AttributeType::getName, AttributeType::getValue));
  }

  private static void assertAttr(Map<String, String> attrMap, UserAttribute attr, String value) {
    assertTrue(attrMap.containsKey(attr.getName()));
    assertThat(attrMap.get(attr.getName()), is(value));
  }

  private CognitoRequestHelper getCognitoRequestHelper() {
    return facade.getCognitoRequestHelper();
  }
}
