package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.ADMIN_ID;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.user;
import static gov.ca.cwds.service.messages.MessageCode.ADMIN_CANNOT_UPDATE_HIMSELF;
import static gov.ca.cwds.service.messages.MessageCode.CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_COUNTY_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.USER_NAME;
import static gov.ca.cwds.util.Utils.toSet;
import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.role.implementor.AdminActionsAuthorizerFactory;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.service.messages.MessagesService.Messages;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "gov.ca.cwds.util.CurrentAuthenticatedUserUtil")
public class AuthorizationServiceImplTest {

  private AuthorizationServiceImpl service;

  private MessagesService messagesServiceMock = mock(MessagesService.class);

  @Before
  public void before() {
    service = new AuthorizationServiceImpl();
    AdminActionsAuthorizerFactory adminActionsAuthorizerFactory = new AdminActionsAuthorizerFactory();
    ExceptionFactory exceptionFactory = new ExceptionFactory();
    exceptionFactory.setMessagesService(messagesServiceMock);
    adminActionsAuthorizerFactory.setExceptionFactory(exceptionFactory);
    service.setAdminRoleImplementorFactory(adminActionsAuthorizerFactory);

    when(messagesServiceMock.getMessages(any(MessageCode.class), ArgumentMatchers.<String>any()))
        .thenReturn(new Messages("techMsg", "userMsg"));

    mockStatic(CurrentAuthenticatedUserUtil.class);
  }

  @Test
  public void testCanUpdateCalsExternalWorkerRole() {
    setUpAdmin(toSet(STATE_ADMIN), "Yolo", toSet("Yolo_2"));

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setRoles(toSet(CWS_WORKER));

    assertCanNotUpdateUser(
        user(toSet(CALS_EXTERNAL_WORKER), "Yolo", "Yolo_1"), userUpdate,
        CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER);
  }

  @Test
  public void testAdminCantUpdateHimself() {
    setUpAdmin(toSet(STATE_ADMIN), "Yolo", toSet("Yolo_2"));
    User user = new User();
    user.setId(ADMIN_ID);
    assertCanNotUpdateUser(user, dummyUserUpdate(), ADMIN_CANNOT_UPDATE_HIMSELF);
  }

  @Test
  public void testStateAdminCanViewUserInSameCounty() {
    setUpAdmin(toSet(STATE_ADMIN, OFFICE_ADMIN), "Yolo", toSet("Yolo_2"));
    assertCanViewUser(user("Yolo", "Yolo_1"));
  }

  @Test
  public void testStateAdminCanViewUserInDifferentCounty() {
    setUpAdmin(toSet(STATE_ADMIN), "Yolo", null);
    assertCanViewUser(user("Madera", "Madera_1"));
  }

  @Test
  public void testStateAdminNoCountyCanViewUser() {
    setUpAdmin(toSet(STATE_ADMIN), null, null);
    assertCanViewUser(user("Madera", "Madera_1"));
  }

  @Test
  public void testCountyAdminCanViewUserInSameCounty() {
    setUpAdmin(toSet(COUNTY_ADMIN, OFFICE_ADMIN), "Yolo", toSet("Yolo_2"));
    assertCanViewUser(user("Yolo", "Yolo_1"));
  }

  @Test
  public void testCountyAdminNoOfficeCanViewUserInSameCounty() {
    setUpAdmin(toSet(COUNTY_ADMIN), "Yolo", null);
    assertCanViewUser(user("Yolo", "Yolo_1"));
  }

  @Test
  public void testCountyAdminCanUpdateCountyAdminInSameCounty() {
    setUpAdmin(toSet(COUNTY_ADMIN), "Yolo", null);
    assertCanUpdateUser(user(toSet(COUNTY_ADMIN), "Yolo", null), dummyUserUpdate());
  }

  @Test
  public void testCountyAdminCanNotUpdateCountyAdminInOtherCounty() {
    setUpAdmin(toSet(COUNTY_ADMIN), "Yolo", null);
    assertCanNotUpdateUser(user(toSet(COUNTY_ADMIN), "Madera", null), dummyUserUpdate(),
        COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY);
  }

  @Test
  public void testCountyAdminCanNotViewUserInDifferentCounty() {
    setUpAdmin(toSet(COUNTY_ADMIN), "Madera", null);

    assertCanNotViewUser(
        user("Yolo", "Yolo_1"),
        COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY);
  }

  @Test
  public void testOfficeAdminCanViewUserInSameOffice() {
    setUpAdmin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1"));
    assertCanViewUser(user("Yolo", "Yolo_1"));
  }

  @Test
  public void testOfficeAdminCanUpdateOfficeAdminInSameOffice() {
    setUpAdmin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1"));
    assertCanUpdateUser(user(toSet(OFFICE_ADMIN), "Yolo", "Yolo_1"), dummyUserUpdate());
  }

  @Test
  public void testOfficeAdminCanNotUpdateOfficeAdminInOtherOffice() {
    setUpAdmin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1"));
    assertCanNotUpdateUser(user(toSet(OFFICE_ADMIN), "Yolo", "Yolo_2"), dummyUserUpdate(),
        OFFICE_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_OFFICE);
  }

  @Test
  public void testOfficeAdminCanNotUpdateCountyAdminInSameOffice() {
    setUpAdmin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1"));
    assertCanNotUpdateUser(user(toSet(COUNTY_ADMIN), "Yolo", "Yolo_1"), dummyUserUpdate(),
        OFFICE_ADMIN_CANNOT_UPDATE_COUNTY_ADMIN);
  }

  @Test
  public void testOfficeAdminCanNotUpdateStateAdminInSameOffice() {
    setUpAdmin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1"));
    assertCanNotUpdateUser(user(toSet(STATE_ADMIN), "Yolo", "Yolo_1"), dummyUserUpdate(),
        OFFICE_ADMIN_CANNOT_UPDATE_STATE_ADMIN);
  }

  @Test
  public void testOfficeAdminCanNotUpdateSuperAdminInSameOffice() {
    setUpAdmin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1"));
    assertCanNotUpdateUser(user(toSet(SUPER_ADMIN), "Yolo", "Yolo_1"), dummyUserUpdate(),
        NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Test
  public void testOfficeAdminCanNotCreateUserInDifferentOffice() {
    setUpAdmin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_2"));
    assertCanNotCreateUser(
        user("Yolo", "Yolo_1"),
        NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE);
  }

  @Test
  public void testOfficeAdminCanNotCreateUserWithNoOffice() {
    setUpAdmin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_2"));
    assertCanNotCreateUser(
        user("Yolo", null),
        NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE
    );
  }

  @Test
  public void testOfficeAdminCanViewUserInSameCounty() {
    setUpAdmin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2"));

    assertCanViewUser(user(toSet(CWS_WORKER), "Yolo", "Yolo_1"));
    assertCanViewUser(
        user(toSet(CWS_WORKER), "Yolo", "Yolo_3"));
    assertCanViewUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_1"));
    assertCanViewUser(
        user(toSet(COUNTY_ADMIN), "Yolo", "Yolo_1"));
    assertCanViewUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_3"));
    assertCanViewUser(
        user(toSet(COUNTY_ADMIN), "Yolo", "Yolo_3"));
  }

  @Test
  public void testCountyAdminCannotUpdateStateAdmin() {
    setUpAdmin(toSet(COUNTY_ADMIN), "Yolo", toSet("Yolo_1"));

    assertCanNotUpdateUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_1"), dummyUserUpdate(),
        COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN);
    assertCanNotUpdateUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_2"), dummyUserUpdate(),
        COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN);
    assertCanNotUpdateUser(
        user(toSet(STATE_ADMIN), "Madura", "Madura_1"), dummyUserUpdate(),
        COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY);
  }

  @Test
  public void testCountyAdminCannotUpdateSuperAdmin() {
    setUpAdmin(toSet(COUNTY_ADMIN), "Yolo", toSet("Yolo_1"));

    assertCanNotUpdateUser(
        user(toSet(SUPER_ADMIN), "Yolo", "Yolo_1"), dummyUserUpdate(),
        NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
    assertCanNotUpdateUser(
        user(toSet(SUPER_ADMIN), "Yolo", "Yolo_2"), dummyUserUpdate(),
        NOT_SUPER_ADMIN_CANNOT_UPDATE_USERS_WITH_SUPER_ADMIN_ROLE);
    assertCanNotUpdateUser(
        user(toSet(SUPER_ADMIN), "Madura", "Madura_1"), dummyUserUpdate(),
        COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY);
  }

  private void assertCanUpdateUser(User user, UserUpdate userUpdate) {
    try {
      service.checkCanUpdateUser(user, userUpdate);
    } catch (AdminAuthorizationException e) {
      fail("AdminAuthorizationException is thrown, code: " + e.getErrorCode());
    }
  }

  private void assertCanNotUpdateUser(User user, UserUpdate userUpdate, MessageCode errorCode) {
    try {
      service.checkCanUpdateUser(user, userUpdate);
      fail("Expected an AdminAuthorizationException to be thrown");
    } catch (AdminAuthorizationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }

  private void assertCanViewUser(User user) {
    assertCan(user, service::checkCanViewUser);
  }

  private void assertCanNotViewUser(User user, MessageCode errorCode) {
    assertCanNot(user, errorCode, service::checkCanViewUser);
  }

  private void assertCanCreateUser(User user) {
    assertCan(user, service::checkCanCreateUser);
  }

  private void assertCanNotCreateUser(User user, MessageCode errorCode) {
    assertCanNot(user, errorCode, service::checkCanCreateUser);
  }

  private <T> void assertCan(T input, Consumer<T> check) {
    try {
      check.accept(input);
    } catch (AdminAuthorizationException e) {
      fail("AdminAuthorizationException is thrown, code: " + e.getErrorCode());
    }
  }

  private <T> void assertCanNot(T input, MessageCode errorCode, Consumer<T> check) {
    try {
      check.accept(input);
      fail("Expected an AdminAuthorizationException to be thrown");
    } catch (AdminAuthorizationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }

  private static void setUpAdmin(Set<String> roles, String countyName, Set<String> adminOfficeIds) {
    UniversalUserToken admin = admin(roles, countyName, adminOfficeIds);
    when(getCurrentUser()).thenReturn(admin);
    when(getCurrentUserName()).thenReturn((String)admin.getParameter(USER_NAME));
    when(getCurrentUserCountyName()).thenReturn(countyName);
    when(getCurrentUserOfficeIds()).thenReturn(adminOfficeIds);
  }

  private static UserUpdate dummyUserUpdate() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setNotes("new notes");
    return userUpdate;
  }
}
