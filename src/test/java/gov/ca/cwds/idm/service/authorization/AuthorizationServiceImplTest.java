package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.user;
import static gov.ca.cwds.service.messages.MessageCode.ADMIN_CANNOT_UPDATE_HIMSELF;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;
import static gov.ca.cwds.util.Utils.toSet;
import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.service.messages.MessagesService.Messages;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
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


  @Before
  public void before() {
    service = new AuthorizationServiceImpl();
    service.setAdminRoleImplementorFactory(new AdminRoleImplementorFactory());

    MessagesService messagesServiceMock = mock(MessagesService.class);
    ExceptionFactory exceptionFactory = new ExceptionFactory();
    exceptionFactory.setMessagesService(messagesServiceMock);
    service.setExceptionFactory(exceptionFactory);

    when(messagesServiceMock.getMessages(any(MessageCode.class), ArgumentMatchers.<String>any()))
        .thenReturn(new Messages("techMsg", "userMsg"));

    mockStatic(CurrentAuthenticatedUserUtil.class);
  }

  @Test
  public void updateCalsExternalWorkerRole() {
    assertCantUpdateRole(CALS_EXTERNAL_WORKER);
  }

  private void assertCantUpdateRole(String... roles) {
    when(getCurrentUser()).thenReturn(
        admin(toSet(STATE_ADMIN), "Yolo", toSet("Yolo_2")));
    assertFalse(service.canEditRoles(user(toSet(roles), "Yolo", "Yolo_1")));
  }

  @Test
  public void testAdminCantUpdateHimself() {
    String adminId = "someId";
    when(CurrentAuthenticatedUserUtil.getCurrentUserName()).thenReturn(adminId);
    User user = new User();
    user.setId(adminId);
    assertCanNotUpdateUser(user, ADMIN_CANNOT_UPDATE_HIMSELF);
  }


  @Test
  public void testByUserAndAdmin_StateAdminSameCounty() {
    when(getCurrentUser()).thenReturn(
        admin(toSet(STATE_ADMIN, OFFICE_ADMIN), "Yolo", toSet("Yolo_2")));
    service.checkCanViewUser(user("Yolo", "Yolo_1"));
  }

  @Test
  public void testByUserAndAdmin_StateAdminDifferentCounty() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(STATE_ADMIN), "Yolo", null));
    service.checkCanViewUser(user("Madera", "Madera_1"));
  }

  @Test
  public void testByUserAndAdmin_StateAdminNoCounty() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(STATE_ADMIN), null, null));
    service.checkCanViewUser(user("Madera", "Madera_1"));
  }

  @Test
  public void testByUserAndAdmin_CountyAdminSameCounty() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(COUNTY_ADMIN, OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
    service.checkCanViewUser(user("Yolo", "Yolo_1"));
  }

  @Test
  public void testByUserAndAdmin_CountyAdminSameCountyNoOffice() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(COUNTY_ADMIN), "Yolo", null));
    when(getCurrentUserCountyName())
        .thenReturn("Yolo");
    service.checkCanViewUser(user("Yolo", "Yolo_1"));
  }

  @Test
  public void testByUserAndAdmin_CountyAdminDifferentCounty() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(COUNTY_ADMIN), "Madera", null));

    assertCanNotViewUser(
        user("Yolo", "Yolo_1"),
        COUNTY_ADMIN_CANNOT_VIEW_USER_FROM_OTHER_COUNTY);
  }

  @Test
  public void testByUserAndAdmin_OfficeAdminSameOffice() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_1"));
    service.checkCanViewUser(user("Yolo", "Yolo_1"));
  }

  @Test
  public void testByUserAndAdmin_OfficeAdminDifferentOffice() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_2")));
    assertCanNotCreateUser(
        user("Yolo", "Yolo_1"),
        NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE);
  }

  @Test
  public void testByUserAndAdmin_OfficeAdmin_UserNoOffice() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_2")));
    assertCanNotCreateUser(
        user("Yolo", null),
        NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE
    );
  }

  @Test
  public void testCanViewUser_OfficeAdmin() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN), "Yolo", toSet("Yolo_1", "Yolo_2")));
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_1", "Yolo_2"));
    when(getCurrentUserCountyName()).thenReturn("Yolo");

    service.checkCanViewUser(user(toSet(CWS_WORKER), "Yolo", "Yolo_1"));
    service.checkCanViewUser(
        user(toSet(CWS_WORKER), "Yolo", "Yolo_3"));
    service.checkCanViewUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_1"));
    service.checkCanViewUser(
        user(toSet(COUNTY_ADMIN), "Yolo", "Yolo_1"));
    service.checkCanViewUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_3"));
    service.checkCanViewUser(
        user(toSet(COUNTY_ADMIN), "Yolo", "Yolo_3"));
  }

  @Test
  public void testCountyAdminCannotUpdateStateAdmin() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(COUNTY_ADMIN), "Yolo", toSet("Yolo_1")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_1"));

    assertCanNotUpdateUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_1"),
        COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN);
    assertCanNotUpdateUser(
        user(toSet(STATE_ADMIN), "Yolo", "Yolo_2"),
        COUNTY_ADMIN_CANNOT_UPDATE_STATE_ADMIN);
    assertCanNotUpdateUser(
        user(toSet(STATE_ADMIN), "Madura", "Madura_1"),
        COUNTY_ADMIN_CANNOT_UPDATE_USER_FROM_OTHER_COUNTY);
  }

  private void assertCanNotUpdateUser(User user, MessageCode errorCode) {
    assertCanNot(user, errorCode, service::checkCanUpdateUser);
  }

  private void assertCanNotViewUser(User user, MessageCode errorCode) {
    assertCanNot(user, errorCode, service::checkCanViewUser);
  }

  private void assertCanNotCreateUser(User user, MessageCode errorCode) {
    assertCanNot(user, errorCode, service::checkCanCreateUser);
  }

  private <T> void assertCanNot(T input, MessageCode errorCode, Consumer<T> check) {
    try {
      check.accept(input);
      fail("Expected an AdminAuthorizationException to be thrown");
    } catch (AdminAuthorizationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }
}
