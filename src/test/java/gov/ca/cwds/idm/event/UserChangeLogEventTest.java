package gov.ca.cwds.idm.event;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.event.UserChangeLogEvent.CAP_EVENT_SOURCE;
import static gov.ca.cwds.idm.event.UserEnabledStatusChangedEvent.ACTIVE;
import static gov.ca.cwds.idm.event.UserEnabledStatusChangedEvent.INACTIVE;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.DatabaseUserAttribute.NOTES;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.config.api.idm.Roles;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserChangeLogRecord;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import gov.ca.cwds.idm.service.cognito.attribute.OtherUserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.CollectionUserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.Diff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.RolesUserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.StringDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.StringUserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserEnabledStatusAttributeDiff;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = {"gov.ca.cwds.util.CurrentAuthenticatedUserUtil",
    "gov.ca.cwds.idm.service.authorization.UserRolesService"})
public class UserChangeLogEventTest {

  private static final String TEST_USER_ID = "testId";
  private static final String TEST_FIRST_NAME = "testFirstName";
  private static final String TEST_LAST_NAME = "testLastName";
  private static final String TEST_COUNTY = "testCounty";
  private static final String TEST_OFFICE_ID = "testOfficeId";
  private static final String PERMISSION_1 = "Permission1";
  private static final String PERMISSION_2 = "Permission2";
  private static final String PERMISSION_3 = "Permission3";
  private static final String PERMISSION_4 = "Permission4";
  private static final String PERMISSION_DESCRIPTION = "description";
  private static final String ADMIN_LOGIN = "TEST_USER_NAME";
  private static final String TEST_ADMIN_ROLE = "Test-admin";
  private static final String TEST_ADMIN_NAME = "Cherno Samba";
  private static final String NEW_EMAIL = "newEmail@gmail.com";
  private static final String OLD_EMAIL = "oldEmail@gmail.com";
  private static final String NEW_NOTES = "new notes";
  private static final String OLD_NOTES = "old notes";

  @Before
  public void before() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    mockStatic(UserRolesService.class);
  }

  @Test
  public void testSetUpUserChangeLogEvent() {
    User user = mockUser();
    UserCreatedEvent userCreatedEvent = new UserCreatedEvent(user);
    UserChangeLogRecord changeLogRecord = userCreatedEvent.getEvent();
    assertEquals(TEST_ADMIN_ROLE, changeLogRecord.getAdminRole());
    assertEquals(TEST_ADMIN_NAME, changeLogRecord.getAdminName());
    assertEquals(TEST_COUNTY, changeLogRecord.getCountyName());
    assertEquals(TEST_OFFICE_ID, changeLogRecord.getOfficeId());
    assertEquals(TEST_USER_ID, changeLogRecord.getUserId());
    assertEquals(TEST_FIRST_NAME + " " + TEST_LAST_NAME, changeLogRecord.getUserName());
    assertEquals(CAP_EVENT_SOURCE, userCreatedEvent.getEventSource());
    assertEquals(ADMIN_LOGIN, userCreatedEvent.getUserLogin());
  }

  @Test
  public void testUserCreatedEvent() {
    User user = mockUser();
    UserCreatedEvent userCreatedEvent = new UserCreatedEvent(user);
    assertEquals(UserCreatedEvent.EVENT_TYPE_USER_CREATED, userCreatedEvent.getEventType());
    assertEquals(String.join(", ", CALS_ADMIN, CWS_WORKER),
        userCreatedEvent.getEvent().getNewValue());
    assertEquals(String.join(", ", CALS_ADMIN, CWS_WORKER),
        userCreatedEvent.getEvent().getUserRoles());
  }

  @Test
  public void testUserRegistrationResentEvent() {
    User user = mockUser();
    UserRegistrationResentEvent event = new UserRegistrationResentEvent(user);
    assertEquals(UserRegistrationResentEvent.EVENT_TYPE_REGISTRATION_RESENT, event.getEventType());
    assertEquals(TEST_FIRST_NAME + " " + TEST_LAST_NAME, event.getEvent().getUserName());
  }

  @Test
  public void testUserRoleChangedEvent() {
    User existedUser = new User();
    existedUser.setRoles(toSet(CALS_ADMIN, CWS_WORKER));
    UserAttributeDiff<Set<String>> diff = new RolesUserAttributeDiff(existedUser,
        existedUser.getRoles(),
        new HashSet<>(Arrays.asList(OFFICE_ADMIN, STATE_ADMIN)));
    UserUpdateRequest userUpdateRequest = mockUserUpdateRequest(Collections.singletonMap(ROLES, diff));
    UserRoleChangedEvent userRoleChangedEvent = new UserRoleChangedEvent(userUpdateRequest);
    assertEquals(UserRoleChangedEvent.EVENT_TYPE_USER_ROLE_CHANGED,
        userRoleChangedEvent.getEventType());
    assertEquals(StringUtils.join(new String[]{"CALS Administrator", "CWS Worker"}, ", "),
        userRoleChangedEvent.getEvent().getOldValue());
    assertEquals("Office Administrator, State Administrator",
        userRoleChangedEvent.getEvent().getNewValue());
    assertEquals(String.join(", ", "Office Administrator", "State Administrator"),
        userRoleChangedEvent.getEvent().getUserRoles());
  }

  @Test
  public void testPermissionsChangedEvent() {
    User existedUser = new User();
    existedUser.setPermissions(toSet(PERMISSION_1, PERMISSION_2));

    UserAttributeDiff<Set<String>> diff = new CollectionUserAttributeDiff(PERMISSIONS, existedUser,
        existedUser.getPermissions(),
        new HashSet<>(Arrays.asList(PERMISSION_3, PERMISSION_4)));

    List<Permission> permissions = Stream.of(
        new Permission(PERMISSION_1, PERMISSION_1 + PERMISSION_DESCRIPTION),
        new Permission(PERMISSION_2, PERMISSION_2 + PERMISSION_DESCRIPTION),
        new Permission(PERMISSION_3, PERMISSION_3 + PERMISSION_DESCRIPTION),
        new Permission(PERMISSION_4, PERMISSION_4 + PERMISSION_DESCRIPTION)
    ).collect(Collectors.toList());
    UserUpdateRequest userUpdateRequest = mockUserUpdateRequest(Collections.singletonMap(PERMISSIONS, diff));

    PermissionsChangedEvent event = new PermissionsChangedEvent(userUpdateRequest, permissions);
    assertEquals(PermissionsChangedEvent.EVENT_TYPE_PERMISSIONS_CHANGED,
        event.getEventType());
    assertEquals(StringUtils.join(
        new String[]{
            PERMISSION_1 + PERMISSION_DESCRIPTION,
            PERMISSION_2 + PERMISSION_DESCRIPTION}, ", "),
        event.getEvent().getOldValue());
    assertEquals(StringUtils.join(
        new String[]{
            PERMISSION_3 + PERMISSION_DESCRIPTION,
            PERMISSION_4 + PERMISSION_DESCRIPTION}, ", "), event.getEvent().getNewValue());
    assertEquals(String.join(", ", CALS_ADMIN, CWS_WORKER),
        event.getEvent().getUserRoles());
  }

  @Test
  public void testEmailChangedEvent() {
    User existedUser = new User();
    existedUser.setEmail(OLD_EMAIL);
    UserAttributeDiff<String> diff = new StringUserAttributeDiff(EMAIL, existedUser,
        OLD_EMAIL, NEW_EMAIL);

    UserUpdateRequest userUpdateRequest = mockUserUpdateRequest(Collections.singletonMap(EMAIL, diff));

    EmailChangedEvent event = new EmailChangedEvent(userUpdateRequest);
    assertEquals(EmailChangedEvent.EVENT_TYPE_EMAIL_CHANGED,
        event.getEventType());
    assertEquals(OLD_EMAIL, event.getEvent().getOldValue());
    assertEquals(NEW_EMAIL, event.getEvent().getNewValue());
    assertEquals(String.join(", ", CALS_ADMIN, CWS_WORKER),
        event.getEvent().getUserRoles());
  }

  @Test
  public void testNotesChangedEvent() {
    UserType existedUser = new UserType();
    StringDiff diff = new StringDiff(OLD_NOTES, NEW_NOTES);
    UserUpdateRequest userUpdateRequest = mockUserUpdateRequestWithDbDiff(existedUser,
         Collections.singletonMap(NOTES, diff));

    NotesChangedEvent event = new NotesChangedEvent(userUpdateRequest);
    assertEquals(NotesChangedEvent.EVENT_TYPE_NOTES_CHANGED, event.getEventType());
    assertEquals(OLD_NOTES, event.getEvent().getOldValue());
    assertEquals(NEW_NOTES, event.getEvent().getNewValue());
    assertEquals(String.join(", ", CALS_ADMIN, CWS_WORKER),
        event.getEvent().getUserRoles());
  }

  @Test
  public void testUserEnabledStatusChangedEvent() {
    User existedUser = new User();
    existedUser.setEnabled(Boolean.FALSE);
    UserAttributeDiff<Boolean> diff =
        new UserEnabledStatusAttributeDiff(existedUser, Boolean.FALSE, Boolean.TRUE);
    UserUpdateRequest userUpdateRequest = mockUserUpdateRequest(
        Collections.singletonMap(OtherUserAttribute.ENABLED_STATUS, diff));

    UserEnabledStatusChangedEvent event = new UserEnabledStatusChangedEvent(userUpdateRequest);
    assertEquals(UserEnabledStatusChangedEvent.USER_ACCOUNT_STATUS_CHANGED,
        event.getEventType());
    assertEquals(INACTIVE, event.getEvent().getOldValue());
    assertEquals(ACTIVE, event.getEvent().getNewValue());
    assertEquals(String.join(", ", CALS_ADMIN, CWS_WORKER),
        event.getEvent().getUserRoles());
  }

  private User mockUser() {
    User user = new User();
    user.setId(TEST_USER_ID);
    user.setFirstName(TEST_FIRST_NAME);
    user.setLastName(TEST_LAST_NAME);
    user.setCountyName(TEST_COUNTY);
    user.setOfficeId(TEST_OFFICE_ID);
    user.setEmail(OLD_EMAIL);
    user.setRoles(new HashSet<>(Arrays.asList(CWS_WORKER, CALS_ADMIN)));
    user.setPermissions(new TreeSet<>(Arrays.asList(PERMISSION_1, PERMISSION_2)));
    when(CurrentAuthenticatedUserUtil.getCurrentUserName()).thenReturn(ADMIN_LOGIN);
    when(CurrentAuthenticatedUserUtil.getCurrentUser()).thenReturn(new UniversalUserToken());
    when(UserRolesService.getStrongestAdminRole(any(UniversalUserToken.class)))
        .thenReturn(TEST_ADMIN_ROLE);
    when(CurrentAuthenticatedUserUtil.getCurrentUserFullName()).thenReturn(TEST_ADMIN_NAME);
    return user;
  }

  private UserUpdateRequest mockUserUpdateRequest(
      Map<UserAttribute, UserAttributeDiff> cognitoDiffMap) {
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
    userUpdateRequest.setExistedUser(mockUser());
    userUpdateRequest.setCognitoDiffMap(cognitoDiffMap);
    return userUpdateRequest;
  }

  private UserUpdateRequest mockUserUpdateRequestWithDbDiff(
      UserType existedUser,
      Map<UserAttribute, Diff> dbDiffMap) {
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
    userUpdateRequest.setExistedUser(mockUser());
    userUpdateRequest.setDatabaseDiffMap(dbDiffMap);
    return userUpdateRequest;
  }
}
