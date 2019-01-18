package gov.ca.cwds.idm.service;

import static gov.ca.cwds.BaseIntegrationTest.H2_DRIVER_CLASS_NAME;
import static gov.ca.cwds.BaseIntegrationTest.IDM_BASIC_AUTH_PASS;
import static gov.ca.cwds.BaseIntegrationTest.IDM_BASIC_AUTH_USER;
import static gov.ca.cwds.idm.service.IdmServiceImpl.transformSearchValues;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.FIRST_NAME;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.RACFID_STANDARD;
import static gov.ca.cwds.idm.util.TestHelper.user;
import static gov.ca.cwds.idm.util.TestHelper.userType;
import static gov.ca.cwds.service.messages.MessageCode.USER_CREATE_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS;
import static gov.ca.cwds.service.messages.MessageCode.USER_CREATE_SAVE_TO_SEARCH_ERROR;
import static gov.ca.cwds.service.messages.MessageCode.USER_PARTIAL_UPDATE;
import static gov.ca.cwds.service.messages.MessageCode.USER_PARTIAL_UPDATE_AND_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS;
import static gov.ca.cwds.service.messages.MessageCode.USER_PARTIAL_UPDATE_AND_SAVE_TO_SEARCH_ERRORS;
import static gov.ca.cwds.service.messages.MessageCode.USER_UPDATE_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS;
import static gov.ca.cwds.util.LiquibaseUtils.TOKEN_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.createTokenStoreDatabase;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserEnableStatusRequest;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.dto.UsersSearchCriteria;
import gov.ca.cwds.idm.exception.PartialSuccessException;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil;
import gov.ca.cwds.idm.util.WithMockCustomUser;
import gov.ca.cwds.service.CwsUserInfoService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles({"dev", "idm"})
@SpringBootTest(properties = {
    "perry.identityManager.idmBasicAuthUser=" + IDM_BASIC_AUTH_USER,
    "perry.identityManager.idmBasicAuthPass=" + IDM_BASIC_AUTH_PASS,
    "perry.identityManager.idmMapping=config/idm.groovy",
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
})
public class IdmServiceImplTest {

  private static final String USER_ID = "17067e4e-270f-4623-b86c-b4d4fa527a34";

  @Autowired
  private IdmServiceImpl service;

  @MockBean
  private CognitoServiceFacade cognitoServiceFacadeMock;

  @MockBean
  private CwsUserInfoService cwsUserInfoServiceMock;

  @MockBean
  private UserLogTransactionalService userLogTransactionalServiceMock;

  @Autowired
  private NsUserService nsUserService;

  @MockBean
  private SearchService searchServiceMock;

  @BeforeClass
  public static void prepareDatabases() throws Exception {
    Class.forName(H2_DRIVER_CLASS_NAME);
    createTokenStoreDatabase();
  }

  @Test
  @WithMockCustomUser
  public void testCreateUserSuccess() {
    User user = user();
    setCreateUserResult(user, USER_ID);

    String id = service.createUser(user);
    assertThat(id, is(USER_ID));
  }

  @Test
  @WithMockCustomUser
  public void testCreateUser_SearchFail() {
    User user = user();
    setCreateUserResult(user, USER_ID);

    Exception doraError = new RuntimeException("Dora error");
    when(searchServiceMock.createUser(any(User.class))).thenThrow(doraError);

    try {
      service.createUser(user);
      fail("should throw PartialSuccessException");
    } catch (PartialSuccessException e) {
      assertThat(e.getUserId(), is(USER_ID));
      assertThat(e.getErrorCode(), is(USER_CREATE_SAVE_TO_SEARCH_ERROR));

      List<Exception> causes = e.getCauses();
      assertThat(causes.size(), is(1));
      assertThat(causes.get(0), is(doraError));
    }
  }

  @Test
  @WithMockCustomUser
  public void testCreateUser_SearchAndDbLogFail() {
    User user = user();
    setCreateUserResult(user, USER_ID);

    Exception doraError = new RuntimeException("Dora error");
    when(searchServiceMock.createUser(any(User.class))).thenThrow(doraError);

    Exception dbError = new RuntimeException("DB error");
    when(userLogTransactionalServiceMock.save(any(UserLog.class))).thenThrow(dbError);

    try {
      service.createUser(user);
      fail("should throw PartialSuccessException");
    } catch (PartialSuccessException e) {
      assertThat(e.getUserId(), is(USER_ID));
      assertThat(e.getErrorCode(), is(USER_CREATE_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS));

      List<Exception> causes = e.getCauses();
      assertThat(causes.size(), is(2));
      assertThat(causes.get(0), is(doraError));
      assertThat(causes.get(1), is(dbError));
    }
  }

  @Test
  @WithMockCustomUser
  public void testUpdateUser_SearchAndDbLogFail() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setPermissions(toSet("Snapshot-rollout"));

    User existedUser = user();
    existedUser.setPermissions(toSet("Hotline-rollout"));
    UserType existedUserType = userType(existedUser, USER_ID);

    setUpdateUserAttributesResult(USER_ID, userUpdate);
    setGetCognitoUserById(USER_ID, existedUserType);

    Exception doraError = new RuntimeException("Dora error");
    when(searchServiceMock.updateUser(any(User.class))).thenThrow(doraError);

    Exception dbError = new RuntimeException("DB error");
    when(userLogTransactionalServiceMock.save(any(UserLog.class))).thenThrow(dbError);

    try {
      service.updateUser(USER_ID, userUpdate);
      fail("should throw PartialSuccessException");
    } catch (PartialSuccessException e) {
      assertThat(e.getUserId(), is(USER_ID));
      assertThat(e.getErrorCode(), is(USER_UPDATE_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS));

      List<Exception> causes = e.getCauses();
      assertThat(causes.size(), is(2));
      assertThat(causes.get(0), is(doraError));
      assertThat(causes.get(1), is(dbError));
    }
  }

  @Test
  @WithMockCustomUser
  public void testPartialUpdateUser() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setPermissions(toSet("Snapshot-rollout"));
    userUpdate.setEnabled(Boolean.FALSE);

    User existedUser = user();
    existedUser.setPermissions(toSet("RFA-rollout"));
    UserType existedUserType = userType(existedUser, USER_ID);

    setUpdateUserAttributesResult(USER_ID, userUpdate);
    setGetCognitoUserById(USER_ID, existedUserType);

    RuntimeException enableStatusError = new RuntimeException("Change Enable Status Error");
    setChangeUserEnabledStatusFail(enableStatusError);

    try {
      service.updateUser(USER_ID, userUpdate);
      fail("should throw PartialSuccessException");
    } catch (PartialSuccessException e) {
      assertThat(e.getUserId(), is(USER_ID));
      assertThat(e.getErrorCode(), is(USER_PARTIAL_UPDATE));

      List<Exception> causes = e.getCauses();
      assertThat(causes.size(), is(1));
      assertThat(causes.get(0), is(enableStatusError));
    }
  }

  @Test
  @WithMockCustomUser
  public void testPartialUpdateUser_SearchFail() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setPermissions(toSet("Snapshot-rollout"));
    userUpdate.setEnabled(Boolean.FALSE);

    User existedUser = user();
    existedUser.setPermissions(toSet("RFA-rollout"));
    UserType existedUserType = userType(existedUser, USER_ID);

    setUpdateUserAttributesResult(USER_ID, userUpdate);
    setGetCognitoUserById(USER_ID, existedUserType);

    RuntimeException enableStatusError = new RuntimeException("Change Enable Status Error");
    setChangeUserEnabledStatusFail(enableStatusError);

    Exception doraError = new RuntimeException("Dora error");
    when(searchServiceMock.updateUser(any(User.class))).thenThrow(doraError);

    try {
      service.updateUser(USER_ID, userUpdate);
      fail("should throw PartialSuccessException");
    } catch (PartialSuccessException e) {
      assertThat(e.getUserId(), is(USER_ID));
      assertThat(e.getErrorCode(), is(USER_PARTIAL_UPDATE_AND_SAVE_TO_SEARCH_ERRORS));

      List<Exception> causes = e.getCauses();
      assertThat(causes.size(), is(2));
      assertThat(causes.get(0), is(enableStatusError));
      assertThat(causes.get(1), is(doraError));
    }
  }

  @Test
  @WithMockCustomUser
  public void testPartialUpdateUser_SearchAndDbLogFail() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setPermissions(toSet("Snapshot-rollout"));
    userUpdate.setEnabled(Boolean.FALSE);

    User existedUser = user();
    existedUser.setPermissions(toSet("RFA-rollout"));
    UserType existedUserType = userType(existedUser, USER_ID);

    setUpdateUserAttributesResult(USER_ID, userUpdate);
    setGetCognitoUserById(USER_ID, existedUserType);

    RuntimeException enableStatusError = new RuntimeException("Change Enable Status Error");
    setChangeUserEnabledStatusFail(enableStatusError);

    Exception doraError = new RuntimeException("Dora error");
    when(searchServiceMock.updateUser(any(User.class))).thenThrow(doraError);

    Exception dbError = new RuntimeException("DB error");
    when(userLogTransactionalServiceMock.save(any(UserLog.class))).thenThrow(dbError);

    try {
      service.updateUser(USER_ID, userUpdate);
      fail("should throw PartialSuccessException");
    } catch (PartialSuccessException e) {
      assertThat(e.getUserId(), is(USER_ID));
      assertThat(e.getErrorCode(), is(USER_PARTIAL_UPDATE_AND_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS));

      List<Exception> causes = e.getCauses();
      assertThat(causes.size(), is(3));
      assertThat(causes.get(0), is(enableStatusError));
      assertThat(causes.get(1), is(doraError));
      assertThat(causes.get(2), is(dbError));
    }
  }

  @Test
  @WithMockCustomUser
  public void testUpdateUser_AttrsNotSetAndEnableStatusError() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEnabled(Boolean.FALSE);

    User existedUser = user();
    existedUser.setPermissions(toSet("old permission"));
    UserType existedUserType = userType(existedUser, USER_ID);

    setGetCognitoUserById(USER_ID, existedUserType);

    RuntimeException enableStatusError = new RuntimeException("Change Enable Status Error");
    setChangeUserEnabledStatusFail(enableStatusError);

    try {
      service.updateUser(USER_ID, userUpdate);
      fail("should throw RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e, is(enableStatusError));
    }
  }

  @Test
  public void testTransformSearchValues() {
    assertThat(
        transformSearchValues(toSet("ROOBLA", "roobla", "Roobla"), RACFID_STANDARD),
        is(toSet("ROOBLA")));
    assertThat(
        transformSearchValues(toSet("some@email.com", "SOME@EMAIL.COM", "Some@email.com"), EMAIL),
        is(toSet("some@email.com")));
    assertThat(
        transformSearchValues(toSet("John", "JOHN", "john"), FIRST_NAME),
        is(toSet("John", "JOHN", "john")));
  }

  @Test
  public void testSearchUsersNoResult() {
    NsUserService spyNsUserService = mock(NsUserService.class, delegatesTo(nsUserService));
    service.setNsUserService(spyNsUserService);
    String racfIdUserAbsent1 = "NORACF";
    String racfIdUserAbsent2 = "NORAC1";
    String racfIdUserPresent = "ROOBLA";
    UsersSearchCriteria searchCriteriaUsersAbsent = new UsersSearchCriteria(RACFID_STANDARD,
        new HashSet<>(
            Arrays.asList(racfIdUserAbsent1, racfIdUserAbsent2)));
    UsersSearchCriteria searchCriteriaUserPresent = new UsersSearchCriteria(RACFID_STANDARD,
        new HashSet<>(
            Collections.singletonList(racfIdUserPresent)));
    CognitoUsersSearchCriteria cognitoSearchCriteriaUserAbsent1 = CognitoUsersSearchCriteriaUtil
        .composeToGetFirstPageByAttribute(RACFID_STANDARD, racfIdUserAbsent1);
    CognitoUsersSearchCriteria cognitoSearchCriteriaUserAbsent2 = CognitoUsersSearchCriteriaUtil
        .composeToGetFirstPageByAttribute(RACFID_STANDARD, racfIdUserAbsent2);
    CognitoUsersSearchCriteria cognitoSearchCriteriaUserPresent = CognitoUsersSearchCriteriaUtil
        .composeToGetFirstPageByAttribute(RACFID_STANDARD, racfIdUserPresent);
    when(cognitoServiceFacadeMock.searchAllPages(cognitoSearchCriteriaUserAbsent1))
        .thenReturn(Collections.emptyList());
    when(cognitoServiceFacadeMock.searchAllPages(cognitoSearchCriteriaUserAbsent2))
        .thenReturn(Collections.emptyList());
    when(cognitoServiceFacadeMock.searchAllPages(cognitoSearchCriteriaUserPresent))
        .thenReturn(Collections.singletonList(userType(user(), USER_ID)));

    service.searchUsers(searchCriteriaUsersAbsent);
    verify(spyNsUserService, never()).findByUsernames(any());
    service.searchUsers(searchCriteriaUserPresent);
    verify(spyNsUserService, times(1)).findByUsernames(any());
  }

  private void setCreateUserResult(User user, String newId) {
    UserType newUser = userType(user, newId);
    when(cognitoServiceFacadeMock.createUser(user)).thenReturn(newUser);
  }

  private void setUpdateUserAttributesResult(String userId, UserUpdate userUpdate) {
    Map<UserAttribute, UserAttributeDiff> result = new HashMap<>();
    result.put(CustomUserAttribute.PHONE_EXTENSION, null);
    when(cognitoServiceFacadeMock
        .updateUserAttributes(eq(userId), any(UserType.class), eq(userUpdate)))
        .thenReturn(result);
  }

  private void setChangeUserEnabledStatusFail(RuntimeException error) {
    when(cognitoServiceFacadeMock.changeUserEnabledStatus(any(UserEnableStatusRequest.class)))
        .thenThrow(error);
  }

  private void setGetCognitoUserById(String userId, UserType result) {
    when(cognitoServiceFacadeMock.getCognitoUserById(userId)).thenReturn(result);
  }
}
