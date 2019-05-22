package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.user;
import static gov.ca.cwds.service.messages.MessageCode.CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_UPDATE_UNALLOWED_ROLES;
import static gov.ca.cwds.util.Utils.toSet;
import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.exception.UserValidationException;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.service.messages.MessagesService.Messages;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "gov.ca.cwds.util.CurrentAuthenticatedUserUtil")
public abstract class BaseAuthorizerTest {

  static final String ADMIN_COUNTY = "Yolo";
  static final String ADMIN_OFFICE = "Yolo_2";

  protected ExceptionFactory exceptionFactory;
  private MessagesService messagesServiceMock = mock(MessagesService.class);

  @Before
  public void beforeParent() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    exceptionFactory = new ExceptionFactory();
    exceptionFactory.setMessagesService(messagesServiceMock);
    when(messagesServiceMock.getMessages(any(MessageCode.class), ArgumentMatchers.<String>any()))
        .thenReturn(new Messages("techMsg", "userMsg"));
  }

  protected AbstractAdminActionsAuthorizer getAuthorizer(User user, UserUpdate userUpdate) {
    AdminActionsAuthorizerFactory adminActionsAuthorizerFactory = new AdminActionsAuthorizerFactory();
    adminActionsAuthorizerFactory.setExceptionFactory(exceptionFactory);
    return adminActionsAuthorizerFactory.getAdminActionsAuthorizer(user, userUpdate);
  }

  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return getAuthorizer(user, new UserUpdate());
  }

  protected void canView(User user) {
    assertCan(getAuthorizer(user)::checkCanViewUser);
  }

  protected void canNotView(User user, MessageCode errorCode) {
    assertAuthorizationException(errorCode, getAuthorizer(user)::checkCanViewUser);
  }

  protected void canCreate(User user) {
    assertCan(getAuthorizer(user)::checkCanCreateUser);
  }

  protected void canUpdateToRole(User user, String newRole) {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setRoles(toSet(newRole));
    getAuthorizer(user, userUpdate).checkCanUpdateUser();
  }

  protected void canNotCreateWithAuthorizationError(User user, MessageCode errorCode) {
    assertAuthorizationException(errorCode,
        getAuthorizer(user)::checkCanCreateUser);
  }

  protected void canNotCreateWithValidationError(User user, MessageCode errorCode) {
    assertValidationException(errorCode,
        getAuthorizer(user)::checkCanCreateUser);
  }

  protected void canUpdate(User user) {
    getAuthorizer(user).checkCanUpdateUser();
  }

  protected void canNotUpdateWithAuthorizationError(User user, MessageCode errorCode) {
    assertAuthorizationException(errorCode, getAuthorizer(user)::checkCanUpdateUser);
  }

  protected void canCreateInAnyCountyAndOffice(String userRole) {
    canCreate(user(toSet(userRole), "SomeCounty", "SomeOffice"));
  }

  protected void canNotUpdateWithAuthorizationError(User user, MessageCode errorCode,
      UserUpdate userUpdate) {
    assertAuthorizationException(errorCode,
        getAuthorizer(user, userUpdate)::checkCanUpdateUser);
  }

  protected void canNotUpdateWithValidationError(User user, MessageCode errorCode,
      UserUpdate userUpdate) {
    assertValidationException(errorCode,
        getAuthorizer(user, userUpdate)::checkCanUpdateUser);
  }

  protected void canNotUpdateToRoleWithValidationError(User user, MessageCode errorCode,
      String newRole) {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setRoles(toSet(newRole));
    canNotUpdateWithValidationError(user, errorCode, userUpdate);
  }

  protected void canNotUpdateToRoleWithAuthorizationError(User user, MessageCode errorCode,
      String newRole) {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setRoles(toSet(newRole));
    canNotUpdateWithAuthorizationError(user, errorCode, userUpdate);
  }

  protected void assertCanNotResendInvitationMessage(User user, MessageCode errorCode) {
    assertAuthorizationException(errorCode, getAuthorizer(user)::checkCanResendInvitationMessage);
  }

  protected void assertCanResendInvitationMessage(User user) {
    assertCan(getAuthorizer(user)::checkCanResendInvitationMessage);
  }

  protected void canNotUpdateCalsExternalWorkerRoleInSameOfficeTo(String newUserRole) {
    canNotUpdateToRoleWithAuthorizationError(
        user(toSet(CALS_EXTERNAL_WORKER), ADMIN_COUNTY, ADMIN_OFFICE),
        CANNOT_EDIT_ROLES_OF_CALS_EXTERNAL_WORKER, newUserRole);
  }

  protected void canNotUpdateCwsWorkerInSameOfficeTo(String newUserRole) {
    canNotUpdateInSameOfficeFromTo(CWS_WORKER, newUserRole);
  }

  protected void canNotUpdateOfficeAdminInSameOfficeTo(String newUserRole) {
    canNotUpdateInSameOfficeFromTo(OFFICE_ADMIN, newUserRole);
  }

  protected void canNotUpdateCountyAdminRoleInSameOfficeTo(String newUserRole) {
    canNotUpdateInSameOfficeFromTo(COUNTY_ADMIN, newUserRole);
  }

  protected void canNotUpdateInSameOfficeFromTo(String oldRole, String newUserRole) {
    canNotUpdateToRoleWithValidationError(
        user(toSet(oldRole), ADMIN_COUNTY, ADMIN_OFFICE),
        UNABLE_UPDATE_UNALLOWED_ROLES, newUserRole);
  }

  protected void canUpdateCwsWorkerInEveryCountyTo(String newRole) {
    canUpdateInEveryCounty(CWS_WORKER, newRole);
  }

  protected void canUpdateCalsExternalWorkerInEveryCountyTo(String newRole) {
    canUpdateInEveryCounty(CALS_EXTERNAL_WORKER, newRole);
  }

  protected void canUpdateInEveryCounty(String oldRole, String newRole) {
    canUpdateToRole(user(toSet(oldRole), "SomeCounty", "SomeOffice"), newRole);
  }

  protected void canUpdateOfficeAdminInEveryCountyTo(String newRole) {
    canUpdateInEveryCounty(OFFICE_ADMIN, newRole);
  }

  protected void canUpdateCountyAdminInEveryCountyTo(String newRole) {
    canUpdateInEveryCounty(COUNTY_ADMIN, newRole);
  }

  protected void canUpdateStateAdminInEveryCountyTo(String newRole) {
    canUpdateInEveryCounty(STATE_ADMIN, newRole);
  }

  private void assertAuthorizationException(MessageCode errorCode, Check check) {
    try {
      check.check();
      fail("Expected an AdminAuthorizationException to be thrown");
    } catch (AdminAuthorizationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }

  private void assertValidationException(MessageCode errorCode, Check check) {
    try {
      check.check();
      fail("Expected an UserValidationException to be thrown");
    } catch (UserValidationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }

  private void assertAuthorizationException(MessageCode errorCode, CheckWithUserUpdate check) {
    try {
      check.check(new UserUpdate());
      fail("Expected an AdminAuthorizationException to be thrown");
    } catch (AdminAuthorizationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }

  private void assertValidationException(MessageCode errorCode, CheckWithUserUpdate check,
      UserUpdate userUpdate) {
    try {
      check.check(userUpdate);
      fail("Expected an UserValidationException to be thrown");
    } catch (UserValidationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }

  private void assertAuthorizationException(MessageCode errorCode, CheckWithUserUpdate check,
      UserUpdate userUpdate) {
    try {
      check.check(userUpdate);
      fail("Expected an AdminAuthorizationException to be thrown");
    } catch (AdminAuthorizationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }

  private void assertCan(Check check) {
    check.check();
  }

  @FunctionalInterface
  interface Check {
    void check();
  }

  @FunctionalInterface
  interface CheckWithUserUpdate {
    void check(UserUpdate userUpdate);
  }
}
