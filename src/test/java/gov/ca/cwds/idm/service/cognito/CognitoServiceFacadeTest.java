package gov.ca.cwds.idm.service.cognito;

import static gov.ca.cwds.idm.service.cognito.CognitoUtils.PERMISSIONS_ATTR_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.service.cognito.CognitoProperties;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.rest.api.domain.UserNotFoundPerryException;
import gov.ca.cwds.service.messages.MessagesService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class CognitoServiceFacadeTest {

  private CognitoServiceFacade fasade;

  private AWSCognitoIdentityProvider identityProvider = mock(AWSCognitoIdentityProvider.class);

  private MessagesService messagesService = mock(MessagesService.class);

  @Before
  public void before() {
    CognitoProperties properties = new CognitoProperties();
    properties.setIamAccessKeyId("iamAccessKeyId");
    properties.setIamSecretKey("iamSecretKey");
    properties.setUserpool("userpool");
    properties.setRegion("us-east-2");

    fasade = new CognitoServiceFacade();
    fasade.setProperties(properties);
    fasade.setIdentityProvider(identityProvider);
    fasade.setMessagesService(messagesService);
  }

  @Test
  public void testInit() {
    fasade.init();
    AWSCognitoIdentityProvider identityProvider = fasade.getIdentityProvider();
    assertThat(identityProvider, is(notNullValue()));
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

    UserType UserType = fasade.getById("id");
    AdminGetUserRequest expectedRequest = new AdminGetUserRequest().withUsername("id")
        .withUserPoolId("userpool");
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

  @Test(expected = UserNotFoundPerryException.class)
  public void testGetByIdUserNotFoundException() {
    when(identityProvider.adminGetUser(any(AdminGetUserRequest.class)))
        .thenThrow(new UserNotFoundException("user not found"));
    fasade.getById("id");
  }

  @Test(expected = PerryException.class)
  public void testGetByIdException() {
    when(identityProvider.adminGetUser(any(AdminGetUserRequest.class)))
        .thenThrow(new RuntimeException());
    fasade.getById("id");
  }

  @Test
  public void testUpdateUserNoChanges() {
    AdminGetUserResult mockResult = new AdminGetUserResult();
    mockResult.setUsername("id");
    mockResult.setEnabled(Boolean.TRUE);

    List<AttributeType> mockAttrs = new ArrayList<>();
    AttributeType mockAttr = new AttributeType();
    mockAttr.setName(PERMISSIONS_ATTR_NAME);
    mockAttr.setValue("Snapshot-rollout:Hotline-rollout");
    mockAttrs.add(mockAttr);
    mockResult.setUserAttributes(mockAttrs);

    when(identityProvider.adminGetUser(any(AdminGetUserRequest.class)))
        .thenReturn(mockResult);

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.TRUE);
    Set<String> permissions = new HashSet<>();
    permissions.add("Snapshot-rollout");
    permissions.add("Hotline-rollout");
    userUpdate.setPermissions(permissions);

    fasade.updateUser("id", userUpdate);

    AdminGetUserRequest expectedRequest = new AdminGetUserRequest().withUsername("id")
        .withUserPoolId("userpool");
    verify(identityProvider, times(1)).adminGetUser(expectedRequest);
    verify(identityProvider, times(0)).updateUserAttributes(any(UpdateUserAttributesRequest.class));
    verify(identityProvider, times(0)).adminDisableUser(any(AdminDisableUserRequest.class));
    verify(identityProvider, times(0)).adminEnableUser(any(AdminEnableUserRequest.class));
  }

  @Test
  public void testUpdateUser() {
    AdminGetUserResult mockResult = new AdminGetUserResult();
    mockResult.setUsername("id");
    mockResult.setEnabled(Boolean.TRUE);

    List<AttributeType> mockAttrs = new ArrayList<>();
    AttributeType mockAttr = new AttributeType();
    mockAttr.setName(PERMISSIONS_ATTR_NAME);
    mockAttr.setValue("Snapshot-rollout");
    mockAttrs.add(mockAttr);
    mockResult.setUserAttributes(mockAttrs);

    when(identityProvider.adminGetUser(any(AdminGetUserRequest.class)))
        .thenReturn(mockResult);

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);
    Set<String> permissions = new HashSet<>();
    permissions.add("Hotline-rollout");
    userUpdate.setPermissions(permissions);

    fasade.updateUser("id", userUpdate);

    AdminGetUserRequest expectedAdminGetUserRequest =
        new AdminGetUserRequest().withUsername("id").withUserPoolId("userpool");
    verify(identityProvider, times(1))
        .adminGetUser(expectedAdminGetUserRequest);


    Collection<AttributeType> expectedUpdateAttributes = new ArrayList<>();
    AttributeType expectedPermissionsAttribute = new AttributeType();
    expectedPermissionsAttribute.setName(PERMISSIONS_ATTR_NAME);
    expectedPermissionsAttribute.setValue("Hotline-rollout");
    expectedUpdateAttributes.add(expectedPermissionsAttribute);

    AdminUpdateUserAttributesRequest expectedAdminUpdateUserAttributesRequest =
        new AdminUpdateUserAttributesRequest()
            .withUsername("id")
            .withUserPoolId("userpool")
            .withUserAttributes(expectedUpdateAttributes);
    verify(identityProvider, times(1))
        .adminUpdateUserAttributes(expectedAdminUpdateUserAttributesRequest);

    AdminDisableUserRequest expectedAdminDisableUserRequest =
        new AdminDisableUserRequest().withUsername("id").withUserPoolId("userpool");
    verify(identityProvider, times(1))
        .adminDisableUser(expectedAdminDisableUserRequest);

    verify(identityProvider, times(0))
        .adminEnableUser(any(AdminEnableUserRequest.class));
  }

  @Test(expected = UserNotFoundPerryException.class)
  public void testUpdateUserNotFoundException() {
    when(identityProvider.adminGetUser(any(AdminGetUserRequest.class)))
        .thenThrow(new UserNotFoundException("user not found"));
    fasade.updateUser("id", new UserUpdate());
  }

  @Test(expected = PerryException.class)
  public void testUpdateUserException() {
    when(identityProvider.adminGetUser(any(AdminGetUserRequest.class)))
        .thenThrow(new RuntimeException());
    fasade.updateUser("id", new UserUpdate());
  }
}
