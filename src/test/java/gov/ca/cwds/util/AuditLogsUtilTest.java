package gov.ca.cwds.util;

import static gov.ca.cwds.util.AuditLogsUtil.CAP_EVENT_SOURCE;
import static gov.ca.cwds.util.AuditLogsUtil.USER_CREATED_EVENT_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.AuditEvent;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserChangeLogRecord;
import gov.ca.cwds.idm.event.UserCreatedEvent;
import gov.ca.cwds.idm.service.authorization.UserRolesService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = {"gov.ca.cwds.util.CurrentAuthenticatedUserUtil",
    "gov.ca.cwds.idm.service.authorization.UserRolesService"})
public class AuditLogsUtilTest {

  private static final String TEST_USER_ID = "testId";
  private static final String TEST_FIRST_NAME = "testFirstName";
  private static final String TEST_LAST_NAME = "testLastName";
  private static final String TEST_COUNTY = "testCounty";
  private static final String TEST_OFFICE_ID = "testOfficeId";
  private static final String ROLE_1 = "Role1";
  private static final String ROLE_2 = "Role2";
  private static final String ADMIN_LOGIN = "TEST_USER_NAME";
  private static final String TEST_ADMIN_ROLE = "Test-admin";
  private static final String TEST_ADMIN_NAME = "Cherno Samba";


  @Before
  public void before() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    mockStatic(UserRolesService.class);
  }

  @Test
  public void testComposeUserCreatedAuditLog() {
    User user = new User();
    user.setId(TEST_USER_ID);
    user.setFirstName(TEST_FIRST_NAME);
    user.setLastName(TEST_LAST_NAME);
    user.setCountyName(TEST_COUNTY);
    user.setOfficeId(TEST_OFFICE_ID);
    user.setRoles(new LinkedHashSet<>(Arrays.asList(ROLE_1, ROLE_2)));
    UserCreatedEvent userCreatedEvent = new UserCreatedEvent(user);

    when(CurrentAuthenticatedUserUtil.getCurrentUserName()).thenReturn(ADMIN_LOGIN);
    when(CurrentAuthenticatedUserUtil.getCurrentUser()).thenReturn(new UniversalUserToken());
    when(UserRolesService.getStrongestAdminRole(any(UniversalUserToken.class)))
        .thenReturn(TEST_ADMIN_ROLE);
    when(CurrentAuthenticatedUserUtil.getCurrentUserFullName()).thenReturn(TEST_ADMIN_NAME);

    AuditEvent result = AuditLogsUtil.composeAuditEvent(userCreatedEvent);

    assertTrue(result.getEvent() instanceof UserChangeLogRecord);
    UserChangeLogRecord changeLogRecord = (UserChangeLogRecord) result.getEvent();

    assertEquals(TEST_ADMIN_ROLE, changeLogRecord.getAdminRole());
    assertEquals(TEST_ADMIN_NAME, changeLogRecord.getAdminName());
    assertEquals(TEST_COUNTY, changeLogRecord.getCountyName());
    assertEquals(String.join(", ", ROLE_1, ROLE_2), changeLogRecord.getNewValue());
    assertEquals(TEST_OFFICE_ID, changeLogRecord.getOfficeId());
    assertEquals(TEST_USER_ID, changeLogRecord.getUserId());

    assertEquals(CAP_EVENT_SOURCE, result.getEventSource());
    assertEquals(USER_CREATED_EVENT_TYPE, result.getEventType());
    assertEquals(ADMIN_LOGIN, result.getUserLogin());
    assertNotNull(result.getId());

  }

}
