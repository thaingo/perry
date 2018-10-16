package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.Utils.toSet;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.AuthorizationTestHelper;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import gov.ca.cwds.rest.api.domain.UserIdmValidationException;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "gov.ca.cwds.util.CurrentAuthenticatedUserUtil")
public class ValidateUpdateUserByAdminRolesServiceTest {

  @Rule
  private ExpectedException expectedException = ExpectedException.none();

  private ValidateUpdateUserByAdminRolesServiceImpl service;
  private MessagesService messagesService;

  @Before
  public void before() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    service = new ValidateUpdateUserByAdminRolesServiceImpl();
    service.setAdminRoleImplementorFactory(new AdminRoleImplementorFactory());
    messagesService = mock(MessagesService.class);
    service.setMessagesService(messagesService);
    when(messagesService.getTechMessage(any(), any())).thenReturn("techMsg");
    when(messagesService.getUserMessage(any(), any())).thenReturn("userMsg");
  }

  @Test
  public void testStateAdminAssign() {
    testAdminCanNotUpdate(admin(STATE_ADMIN), user(STATE_ADMIN));
    testAdminCanUpdate(admin(STATE_ADMIN), user(COUNTY_ADMIN));
    testAdminCanUpdate(admin(STATE_ADMIN), user(OFFICE_ADMIN));
    testAdminCanUpdate(admin(STATE_ADMIN), user(CWS_WORKER));
    testAdminCanNotUpdate(admin(STATE_ADMIN), user());
  }

  @Test
  public void testCountyAdminAssign() {
    testAdminCanNotUpdate(admin(COUNTY_ADMIN), user(STATE_ADMIN));
    testAdminCanNotUpdate(admin(COUNTY_ADMIN), user(COUNTY_ADMIN));
    testAdminCanUpdate(admin(COUNTY_ADMIN), user(OFFICE_ADMIN));
    testAdminCanUpdate(admin(COUNTY_ADMIN), user(CWS_WORKER));
    testAdminCanNotUpdate(admin(COUNTY_ADMIN), user());
  }

  @Test
  public void testOfficeAdminAssignStateAdmin() {
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), user(STATE_ADMIN));
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), user(COUNTY_ADMIN));
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), user(OFFICE_ADMIN));
    testAdminCanUpdate(admin(OFFICE_ADMIN), user(CWS_WORKER));
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), user());
  }

  private void testAdminCanUpdate(UniversalUserToken admin, User user) {
    validateUpdate(admin, user);
  }

  private void testAdminCanNotUpdate(UniversalUserToken admin, User user) {
    expectedException.expect(UserIdmValidationException.class);
    expectedException.expectMessage("techMsg");
    validateUpdate(admin, user);
  }

  private void validateUpdate(UniversalUserToken admin, User user) {
    when(getCurrentUser()).thenReturn(admin);
    service.validateUpdateUser(user);
  }

  private static UniversalUserToken admin(String... roles) {
    UniversalUserToken admin =
        AuthorizationTestHelper.admin(toSet(roles), "Yolo", toSet("Yolo_1"));
    admin.setUserId("adminId");
    return admin;
  }

  private static User user(String... roles) {
    User user = AuthorizationTestHelper.user(toSet(roles), "Madera", "Madera_1");
    user.setId("userId");
    return user;
  }
}
