package gov.ca.cwds.idm.event;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.getRoleNameById;
import static gov.ca.cwds.idm.event.SystemCausedChangeLogEvent.SYSTEM_USER_LOGIN;
import static gov.ca.cwds.idm.event.UserChangeLogEvent.CAP_EVENT_SOURCE;
import static gov.ca.cwds.idm.event.UserEnabledStatusChangedEvent.ACTIVE;
import static gov.ca.cwds.idm.event.UserEnabledStatusChangedEvent.INACTIVE;
import static gov.ca.cwds.idm.event.UserLockedEvent.LOCKED;
import static gov.ca.cwds.idm.event.UserLockedEvent.UNLOCKED;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserChangeLogRecord;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    assertEquals(String.join(", ", getRoleNameById(CWS_WORKER), getRoleNameById(CALS_ADMIN)),
        userCreatedEvent.getEvent().getUserRoles());
  }

  @Test
  public void testUserRegistrationResentEvent() {
    User user = mockUser();
    UserRegistrationResentEvent event = new UserRegistrationResentEvent(user);
    assertEquals(UserRegistrationResentEvent.EVENT_TYPE_REGISTRATION_RESENT, event.getEventType());
    assertEquals(TEST_FIRST_NAME + " " + TEST_LAST_NAME, event.getEvent().getUserName());
    assertEquals(String.join(", ", getRoleNameById(CWS_WORKER), getRoleNameById(CALS_ADMIN)),
        event.getEvent().getUserRoles());
  }

  @Test
  public void testUserRoleChangedEvent() {
    StringSetDiff diff = new StringSetDiff(
        toSet(CALS_ADMIN, CWS_WORKER), toSet(OFFICE_ADMIN, STATE_ADMIN));

    UserRoleChangedEvent userRoleChangedEvent = new UserRoleChangedEvent(mockUser(), diff);
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

    StringSetDiff diff = new StringSetDiff(toSet(PERMISSION_1, PERMISSION_2),
        toSet(PERMISSION_3, PERMISSION_4));

    List<Permission> permissions = Stream.of(
        new Permission(PERMISSION_1, PERMISSION_1 + PERMISSION_DESCRIPTION),
        new Permission(PERMISSION_2, PERMISSION_2 + PERMISSION_DESCRIPTION),
        new Permission(PERMISSION_3, PERMISSION_3 + PERMISSION_DESCRIPTION),
        new Permission(PERMISSION_4, PERMISSION_4 + PERMISSION_DESCRIPTION)
    ).collect(Collectors.toList());

    PermissionsChangedEvent event = new PermissionsChangedEvent(mockUser(), diff, permissions);
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
    assertEquals(String.join(", ", getRoleNameById(CWS_WORKER), getRoleNameById(CALS_ADMIN)),
        event.getEvent().getUserRoles());
  }

  @Test
  public void testEmailChangedEvent() {

    StringDiff diff = new StringDiff(OLD_EMAIL, NEW_EMAIL);

    EmailChangedEvent event = new EmailChangedEvent(mockUser(), diff);
    assertEquals(EmailChangedEvent.EVENT_TYPE_EMAIL_CHANGED,
        event.getEventType());
    assertEquals(OLD_EMAIL, event.getEvent().getOldValue());
    assertEquals(NEW_EMAIL, event.getEvent().getNewValue());
    assertEquals(String.join(", ", getRoleNameById(CWS_WORKER), getRoleNameById(CALS_ADMIN)),
        event.getEvent().getUserRoles());
  }

  @Test
  public void testNotesChangedEvent() {
    StringDiff diff = new StringDiff(OLD_NOTES, NEW_NOTES);

    NotesChangedEvent event = new NotesChangedEvent(mockUser(), diff);
    assertEquals(NotesChangedEvent.EVENT_TYPE_NOTES_CHANGED, event.getEventType());
    assertEquals(OLD_NOTES, event.getEvent().getOldValue());
    assertEquals(NEW_NOTES, event.getEvent().getNewValue());
    assertEquals(String.join(", ", getRoleNameById(CWS_WORKER), getRoleNameById(CALS_ADMIN)),
        event.getEvent().getUserRoles());
  }

  @Test
  public void testUserEnabledStatusChangedEvent() {
    BooleanDiff diff = new BooleanDiff(Boolean.FALSE, Boolean.TRUE);

    UserEnabledStatusChangedEvent event = new UserEnabledStatusChangedEvent(mockUser(), diff);
    assertEquals(UserEnabledStatusChangedEvent.USER_ACCOUNT_STATUS_CHANGED,
        event.getEventType());
    assertEquals(INACTIVE, event.getEvent().getOldValue());
    assertEquals(ACTIVE, event.getEvent().getNewValue());
    assertEquals(String.join(", ", getRoleNameById(CWS_WORKER), getRoleNameById(CALS_ADMIN)),
        event.getEvent().getUserRoles());

  }

  @Test
  public void testUserLockedEvent() {
    UserLockedEvent event = new UserLockedEvent(mockUser());
    assertEquals(UserLockedEvent.EVENT_TYPE_USER_LOCKED,
        event.getEventType());
    assertEquals(UNLOCKED, event.getEvent().getOldValue());
    assertEquals(LOCKED, event.getEvent().getNewValue());
    assertEquals(SYSTEM_USER_LOGIN, event.getUserLogin());

  }

  @Test
  public void testUserUnlockedEvent() {
    UserUnlockedEvent event = new UserUnlockedEvent(mockUser());
    assertEquals(UserUnlockedEvent.EVENT_TYPE_USER_UNLOCKED, event.getEventType());
    assertEquals(LOCKED, event.getEvent().getOldValue());
    assertEquals(UNLOCKED, event.getEvent().getNewValue());
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
}
