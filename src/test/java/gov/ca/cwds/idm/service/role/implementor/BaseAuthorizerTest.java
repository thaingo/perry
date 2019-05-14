package gov.ca.cwds.idm.service.role.implementor;

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

  protected abstract AbstractAdminActionsAuthorizer getAuthorizer(User user);

  private AbstractAdminActionsAuthorizer getAuthorizerWithExceptionFactory(User user) {
    AbstractAdminActionsAuthorizer authorizer = getAuthorizer(user);
    authorizer.setExceptionFactory(exceptionFactory);
    return authorizer;
  }

  protected void assertCanNotViewUser(User user, MessageCode errorCode) {
    assertCanNot(errorCode, getAuthorizerWithExceptionFactory(user)::checkCanViewUser);
  }

  protected void assertCanViewUser(User user) {
    assertCan(getAuthorizerWithExceptionFactory(user)::checkCanViewUser);
  }

  protected void assertCanNotCreateUser(User user, MessageCode errorCode) {
    assertCanNot(errorCode, getAuthorizerWithExceptionFactory(user)::checkCanCreateUser);
  }

  protected void assertCanCreateUser(User user) {
    assertCan(getAuthorizerWithExceptionFactory(user)::checkCanCreateUser);
  }

  protected void assertCanNotUpdateUser(User user, MessageCode errorCode) {
    assertCanNot(errorCode, getAuthorizerWithExceptionFactory(user)::checkCanUpdateUser);
  }

  protected void assertCanUpdateUser(User user) {
    assertCan(getAuthorizerWithExceptionFactory(user)::checkCanUpdateUser);
  }

  protected void assertCanNotResendInvitationMessage(User user, MessageCode errorCode) {
    assertCanNot(errorCode, getAuthorizerWithExceptionFactory(user)::checkCanResendInvitationMessage);
  }

  protected void assertCanResendInvitationMessage(User user) {
    assertCan(getAuthorizerWithExceptionFactory(user)::checkCanResendInvitationMessage);
  }

  private void assertCanNot(MessageCode errorCode, Check check) {
    try {
      check.check();
      fail("Expected an AdminAuthorizationException to be thrown");
    } catch (AdminAuthorizationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }

  private void assertCanNot(MessageCode errorCode, CheckWithUserUpdate check) {
    try {
      check.check(new UserUpdate());
      fail("Expected an AdminAuthorizationException to be thrown");
    } catch (AdminAuthorizationException e) {
      assertThat(e.getErrorCode(), is(errorCode));
    }
  }

  private void assertCan(Check check) {
    check.check();
  }

  private void assertCan(CheckWithUserUpdate check) {
    check.check(new UserUpdate());
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
