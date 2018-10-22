package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.TestHelper.userType;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.Utils.toSet;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.TestHelper;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import gov.ca.cwds.rest.api.domain.UserIdmValidationException;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "gov.ca.cwds.util.CurrentAuthenticatedUserUtil")
public class ValidationServiceMockTest {

  private static final String USER_ID = "17067e4e-270f-4623-b86c-b4d4fa527a34";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private ValidationServiceImpl service;

  private MessagesService messagesServiceMock = mock(MessagesService.class);

  @Before
  public void before() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    service = new ValidationServiceImpl();
    service.setMessagesService(messagesServiceMock);
    service.setAdminRoleImplementorFactory(new AdminRoleImplementorFactory());
  }

  @Test
  public void testStateAdminAssign() {
    testAdminCanNotUpdate(admin(STATE_ADMIN), userUpdate(STATE_ADMIN));
    testAdminCanUpdate(admin(STATE_ADMIN), userUpdate(COUNTY_ADMIN));
    testAdminCanUpdate(admin(STATE_ADMIN), userUpdate(OFFICE_ADMIN));
    testAdminCanUpdate(admin(STATE_ADMIN), userUpdate(CWS_WORKER));
    testAdminCanNotUpdate(admin(STATE_ADMIN), userUpdate());
  }

  @Test
  public void testCountyAdminAssign() {
    testAdminCanNotUpdate(admin(COUNTY_ADMIN), userUpdate(STATE_ADMIN));
    testAdminCanNotUpdate(admin(COUNTY_ADMIN), userUpdate(COUNTY_ADMIN));
    testAdminCanUpdate(admin(COUNTY_ADMIN), userUpdate(OFFICE_ADMIN));
    testAdminCanUpdate(admin(COUNTY_ADMIN), userUpdate(CWS_WORKER));
    testAdminCanNotUpdate(admin(COUNTY_ADMIN), userUpdate());
  }

  @Test
  public void testOfficeAdminAssignStateAdmin() {
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), userUpdate(STATE_ADMIN));
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), userUpdate(COUNTY_ADMIN));
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), userUpdate(OFFICE_ADMIN));
    testAdminCanUpdate(admin(OFFICE_ADMIN), userUpdate(CWS_WORKER));
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), userUpdate());
  }

  private void testAdminCanUpdate(UniversalUserToken admin, UserUpdate userUpdate) {
    validateUpdate(admin, userUpdate);
  }

  private void testAdminCanNotUpdate(UniversalUserToken admin, UserUpdate userUpdate) {
    expectedException.expect(UserIdmValidationException.class);
    validateUpdate(admin, userUpdate);
  }

  private void validateUpdate(UniversalUserToken admin, UserUpdate userUpdate) {
    PowerMockito.when(getCurrentUser()).thenReturn(admin);
    UserType userType = userType(user(), USER_ID);
    service.validateUpdateUser(admin, userType, userUpdate);
  }

  private static UniversalUserToken admin(String... roles) {
    UniversalUserToken admin =
        TestHelper.admin(toSet(roles), "Yolo", toSet("Yolo_1"));
    admin.setUserId("adminId");
    return admin;
  }

  private static User user(String... roles) {
    User user = TestHelper.user(toSet(roles), "Madera", "Madera_1");
    user.setId("userId");
    return user;
  }

  private static UserUpdate userUpdate(String... roles) {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setRoles(toSet(roles));
    return userUpdate;
  }
}
