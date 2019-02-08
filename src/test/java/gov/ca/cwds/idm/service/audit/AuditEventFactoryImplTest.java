package gov.ca.cwds.idm.service.audit;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.getRoleNameById;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.convertRoleKeysToNamesString;
import static gov.ca.cwds.idm.service.audit.event.UserAuditEvent.CAP_EVENT_SOURCE;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.ACTIVE;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.EVENT_TYPE_EMAIL_CHANGED;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.EVENT_TYPE_PERMISSIONS_CHANGED;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.EVENT_TYPE_REGISTRATION_RESENT;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.EVENT_TYPE_USER_ENABLED_STATUS_CHANGED;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.EVENT_TYPE_USER_ROLE_CHANGED;
import static gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl.INACTIVE;
import static gov.ca.cwds.util.Utils.toSet;
import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserChangeLogRecord;
import gov.ca.cwds.idm.service.DictionaryProvider;
import gov.ca.cwds.idm.service.audit.event.UserAuditEvent;
import gov.ca.cwds.idm.service.audit.event.UserPropertyChangedAuditEvent;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.audit.AuditEventFactoryImpl;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.util.Arrays;
import java.util.Collections;
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
public class AuditEventFactoryImplTest {

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

  private AuditEventFactoryImpl auditEventFactory;

  private DictionaryProvider dictionaryProvider = mock(DictionaryProvider.class);

  @Before
  public void before() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    mockStatic(UserRolesService.class);
    auditEventFactory = new AuditEventFactoryImpl();
    auditEventFactory.setDictionaryProvider(dictionaryProvider);
  }

  @Test
  public void testCreateUserEvent() {
    User user = mockUser();
    UserAuditEvent event =
        auditEventFactory.createUserEvent(EVENT_TYPE_REGISTRATION_RESENT, user);

    assertCommonEventProperties(event);
    assertOldRoles(event);
    assertEquals(EVENT_TYPE_REGISTRATION_RESENT, event.getEventType());
  }

  @Test
  public void testUserRoleChangedEvent() {
    StringSetDiff diff = new StringSetDiff(
        toSet(CALS_ADMIN, CWS_WORKER), toSet(OFFICE_ADMIN, STATE_ADMIN));

    UserPropertyChangedAuditEvent userRoleChangedEvent = auditEventFactory
        .createUserRoleChangedEvent(mockUser(), diff);

    assertCommonEventProperties(userRoleChangedEvent);
    assertEquals(EVENT_TYPE_USER_ROLE_CHANGED,
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

    List<Permission> permissions = Stream.of(
        new Permission(PERMISSION_1, PERMISSION_1 + PERMISSION_DESCRIPTION),
        new Permission(PERMISSION_2, PERMISSION_2 + PERMISSION_DESCRIPTION),
        new Permission(PERMISSION_3, PERMISSION_3 + PERMISSION_DESCRIPTION),
        new Permission(PERMISSION_4, PERMISSION_4 + PERMISSION_DESCRIPTION)
    ).collect(Collectors.toList());

    when(dictionaryProvider.getPermissions()).thenReturn(permissions);

    StringSetDiff diff = new StringSetDiff(toSet(PERMISSION_1, PERMISSION_2), toSet(PERMISSION_3, PERMISSION_4));

    UserPropertyChangedAuditEvent event = auditEventFactory.createUpdatePermissionsEvent(mockUser(), diff);

    assertCommonEventProperties(event);
    assertOldRoles(event);
    assertEquals(EVENT_TYPE_PERMISSIONS_CHANGED,
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
  }

  @Test
  public void testCreateUserPropertyChangedEvent() {
    StringDiff diff = new StringDiff(OLD_EMAIL, NEW_EMAIL);

    UserPropertyChangedAuditEvent event = auditEventFactory
        .createUserPropertyChangedEvent(EVENT_TYPE_EMAIL_CHANGED, mockUser(), diff);

    assertCommonEventProperties(event);
    assertOldRoles(event);
    assertEquals(EVENT_TYPE_EMAIL_CHANGED, event.getEventType());
    assertEquals(OLD_EMAIL, event.getEvent().getOldValue());
    assertEquals(NEW_EMAIL, event.getEvent().getNewValue());
  }

  @Test
  public void testUserEnabledStatusChangedEvent() {
    BooleanDiff diff = new BooleanDiff(Boolean.FALSE, Boolean.TRUE);

    UserPropertyChangedAuditEvent event =
        auditEventFactory.createUserEnableStatusUpdateEvent(mockUser(), diff);

    assertCommonEventProperties(event);
    assertOldRoles(event);
    assertEquals(EVENT_TYPE_USER_ENABLED_STATUS_CHANGED, event.getEventType());
    assertEquals(INACTIVE, event.getEvent().getOldValue());
    assertEquals(ACTIVE, event.getEvent().getNewValue());
  }

  @Test
  public void testConvertRoleKeysToNamesString() {
    assertThat(convertRoleKeysToNamesString(null), is(""));
    assertThat(convertRoleKeysToNamesString(emptySet()), is(""));
    assertThat(convertRoleKeysToNamesString(toSet(CWS_WORKER)), is("CWS Worker"));
    assertThat(convertRoleKeysToNamesString(toSet(OFFICE_ADMIN, STATE_ADMIN, CWS_WORKER)),
        is("CWS Worker, Office Administrator, State Administrator"));
  }

  private void assertCommonEventProperties(UserAuditEvent userAuditEvent) {
    UserChangeLogRecord changeLogRecord = userAuditEvent.getEvent();
    assertEquals(TEST_ADMIN_ROLE, changeLogRecord.getAdminRole());
    assertEquals(TEST_ADMIN_NAME, changeLogRecord.getAdminName());
    assertEquals(TEST_COUNTY, changeLogRecord.getCountyName());
    assertEquals(TEST_OFFICE_ID, changeLogRecord.getOfficeId());
    assertEquals(TEST_USER_ID, changeLogRecord.getUserId());
    assertEquals(TEST_FIRST_NAME + " " + TEST_LAST_NAME, changeLogRecord.getUserName());
    assertEquals(CAP_EVENT_SOURCE, userAuditEvent.getEventSource());
    assertEquals(ADMIN_LOGIN, userAuditEvent.getUserLogin());
  }

  private void assertOldRoles(UserAuditEvent userAuditEvent) {
    assertEquals(String.join(", ", getRoleNameById(CWS_WORKER), getRoleNameById(CALS_ADMIN)),
        userAuditEvent.getEvent().getUserRoles());
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
