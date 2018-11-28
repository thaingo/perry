package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.admin;
import static gov.ca.cwds.idm.util.TestHelper.user;
import static gov.ca.cwds.service.messages.MessageCode.OFFICE_ADMIN_CANNOT_UPDATE_ADMIN;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserOfficeIds;
import static gov.ca.cwds.util.Utils.toSet;
import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.service.messages.MessagesService.Messages;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "gov.ca.cwds.util.CurrentAuthenticatedUserUtil")
public class OfficeAdminAuthorizerTest {

  private ExceptionFactory exceptionFactory;

  private MessagesService messagesServiceMock = mock(MessagesService.class);

  @Before
  public void mockOfficeAdmin() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    exceptionFactory = new ExceptionFactory();
    exceptionFactory.setMessagesService(messagesServiceMock);
    when(messagesServiceMock.getMessages(any(MessageCode.class), ArgumentMatchers.<String>any()))
        .thenReturn(new Messages("techMsg", "userMsg"));
  }

  @Test
  public void canEditRoles() {
    assertTrue(new OfficeAdminAuthorizer(user("Yolo", "Yolo_1")).canEditRoles());
  }

  @Test
  public void canNotEditStateAdminTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    assertCannotUpdateUser(
        user(toSet(STATE_ADMIN),"Yolo", "Yolo_2"),
        OFFICE_ADMIN_CANNOT_UPDATE_ADMIN);
  }

  @Test
  public void canEditCwsWorkerTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    assertCanUpdateUser(user(toSet(CWS_WORKER),"Yolo", "Yolo_2"));
  }

  @Test
  public void canNotEditCountyAdminTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    assertCannotUpdateUser(
        user(toSet(COUNTY_ADMIN),"Yolo", "Yolo_2"),
        OFFICE_ADMIN_CANNOT_UPDATE_ADMIN);
  }

  @Test
  public void canNotEditOfficeAdminTest() {
    when(getCurrentUser())
        .thenReturn(admin(toSet(OFFICE_ADMIN),
            "Yolo", toSet("Yolo_2")));
    when(getCurrentUserOfficeIds()).thenReturn(toSet("Yolo_2"));
    when(getCurrentUserCountyName()).thenReturn("Yolo");
    assertCannotUpdateUser(
        user(toSet(OFFICE_ADMIN),"Yolo", "Yolo_2"),
        OFFICE_ADMIN_CANNOT_UPDATE_ADMIN
        );
  }

  private void assertCannotUpdateUser(User user, MessageCode errorCode) {
    try {
      OfficeAdminAuthorizer authorizer = new OfficeAdminAuthorizer(user);
      authorizer.setExceptionFactory(exceptionFactory);
      authorizer.checkCanUpdateUser();
      fail("Expected an AdminAuthorizationException to be thrown");
    } catch (AdminAuthorizationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }

  private void assertCanUpdateUser(User user) {
    OfficeAdminAuthorizer authorizer = new OfficeAdminAuthorizer(user);
    authorizer.setExceptionFactory(exceptionFactory);
    authorizer.checkCanUpdateUser();
  }
}