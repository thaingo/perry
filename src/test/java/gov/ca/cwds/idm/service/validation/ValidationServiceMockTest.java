package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.service.messages.MessageCode.INVALID_PHONE_EXTENSION_FORMAT;
import static gov.ca.cwds.service.messages.MessageCode.INVALID_PHONE_FORMAT;
import static gov.ca.cwds.service.messages.MessageCode.USER_PHONE_IS_NOT_PROVIDED;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.exception.UserValidationException;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import gov.ca.cwds.idm.util.TestHelper;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.service.messages.MessagesService.Messages;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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
    ExceptionFactory exceptionFactory = new ExceptionFactory();
    exceptionFactory.setMessagesService(messagesServiceMock);
    service.setExceptionFactory(exceptionFactory);
    service.setAdminRoleImplementorFactory(new AdminRoleImplementorFactory());

    when(messagesServiceMock.getMessages(any(MessageCode.class), ArgumentMatchers.<String>any()))
        .thenReturn(new Messages("", ""));
  }

  @Test
  public void testStateAdminUpdate() {
    testAdminCanNotUpdate(admin(STATE_ADMIN), userUpdate(STATE_ADMIN));
    testAdminCanUpdate(admin(STATE_ADMIN), userUpdate(COUNTY_ADMIN));
    testAdminCanUpdate(admin(STATE_ADMIN), userUpdate(OFFICE_ADMIN));
    testAdminCanUpdate(admin(STATE_ADMIN), userUpdate(CWS_WORKER));
    testAdminCanNotUpdate(admin(STATE_ADMIN), userUpdate());
    testAdminCanNotUpdate(admin(STATE_ADMIN), userUpdate(CALS_EXTERNAL_WORKER));
  }

  @Test
  public void testCountyAdminUpdate() {
    testAdminCanNotUpdate(admin(COUNTY_ADMIN), userUpdate(STATE_ADMIN));
    testAdminCanNotUpdate(admin(COUNTY_ADMIN), userUpdate(COUNTY_ADMIN));
    testAdminCanUpdate(admin(COUNTY_ADMIN), userUpdate(OFFICE_ADMIN));
    testAdminCanUpdate(admin(COUNTY_ADMIN), userUpdate(CWS_WORKER));
    testAdminCanNotUpdate(admin(COUNTY_ADMIN), userUpdate());
    testAdminCanNotUpdate(admin(COUNTY_ADMIN), userUpdate(CALS_EXTERNAL_WORKER));
  }

  @Test
  public void testOfficeAdminUpdate() {
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), userUpdate(STATE_ADMIN));
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), userUpdate(COUNTY_ADMIN));
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), userUpdate(OFFICE_ADMIN));
    testAdminCanUpdate(admin(OFFICE_ADMIN), userUpdate(CWS_WORKER));
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), userUpdate());
    testAdminCanNotUpdate(admin(OFFICE_ADMIN), userUpdate(CALS_EXTERNAL_WORKER));
  }

  @Test
  public void testStateAdminCreate() {
    testAdminCanNotCreate(admin(STATE_ADMIN), user(STATE_ADMIN));
    testAdminCanCreate(admin(STATE_ADMIN), user(COUNTY_ADMIN));
    testAdminCanCreate(admin(STATE_ADMIN), user(OFFICE_ADMIN));
    testAdminCanCreate(admin(STATE_ADMIN), user(CWS_WORKER));
    testAdminCanNotCreate(admin(STATE_ADMIN), user());
    testAdminCanNotCreate(admin(STATE_ADMIN), user(CALS_EXTERNAL_WORKER));
  }

  @Test
  public void testCountyAdminCreate() {
    testAdminCanNotCreate(admin(COUNTY_ADMIN), user(STATE_ADMIN));
    testAdminCanNotCreate(admin(COUNTY_ADMIN), user(COUNTY_ADMIN));
    testAdminCanCreate(admin(COUNTY_ADMIN), user(OFFICE_ADMIN));
    testAdminCanCreate(admin(COUNTY_ADMIN), user(CWS_WORKER));
    testAdminCanNotCreate(admin(COUNTY_ADMIN), user());
    testAdminCanNotCreate(admin(COUNTY_ADMIN), user(CALS_EXTERNAL_WORKER));
  }

  @Test
  public void testOfficeAdminCreate() {
    testAdminCanNotCreate(admin(OFFICE_ADMIN), user(STATE_ADMIN));
    testAdminCanNotCreate(admin(OFFICE_ADMIN), user(COUNTY_ADMIN));
    testAdminCanNotCreate(admin(OFFICE_ADMIN), user(OFFICE_ADMIN));
    testAdminCanCreate(admin(OFFICE_ADMIN), user(CWS_WORKER));
    testAdminCanNotCreate(admin(OFFICE_ADMIN), user());
    testAdminCanNotCreate(admin(OFFICE_ADMIN), user(CALS_EXTERNAL_WORKER));
  }

  @Test
  public void canNotUpdate_emptyPhoneNumber() {
    canNotUpdatePhoneNumber("", USER_PHONE_IS_NOT_PROVIDED);
  }

  @Test
  public void canNotUpdate_blankPhoneNumber() {
    canNotUpdatePhoneNumber("   ", USER_PHONE_IS_NOT_PROVIDED);
  }

  @Test
  public void canNotUpdate_phoneNumberWithNonDigits() {
    canNotUpdatePhoneNumber("abc-456789", INVALID_PHONE_FORMAT);
  }

  @Test
  public void canNotUpdate_phoneNumberWithMoreThenTenChars() {
    canNotUpdatePhoneNumber("12345678901", INVALID_PHONE_FORMAT);
  }

  @Test
  public void canNotUpdatep_phoneNumberStartingWithZero(){
    canNotUpdatePhoneNumber("0123456789", INVALID_PHONE_FORMAT);
  }

  @Test
  public void canNotUpdate_phoneNumberWithLessThenTenChars() {
    canNotUpdatePhoneNumber("123", INVALID_PHONE_FORMAT);
  }

  @Test
  public void canUpdate_phoneNumberWithTenChars(){
    canUpdatePhoneNumber("1234567890");
  }

  @Test
  public void canUpdate_emptyPhoneExtension() {
    canUpdatePhoneExtension("");
  }

  @Test
  public void canUpdate_blankPhoneExtension() {
    canUpdatePhoneExtension("   ");
  }

  @Test
  public void canNotUpdate_phoneExtensionWithNonDigits() {
    canNotUpdatePhoneExtension("a-45", INVALID_PHONE_EXTENSION_FORMAT);
  }

  @Test
  public void canNotUpdate_phoneExtensionWithMoreThenSevenChars() {
    canNotUpdatePhoneExtension("12345678", INVALID_PHONE_EXTENSION_FORMAT);
  }

  @Test
  public void canUpdate_phoneExtenionWithLessThenSevenChars() {
    canUpdatePhoneExtension("123");
  }
  @Test
  public void canUpdate_phoneExtensionWithSevenChars(){
    canUpdatePhoneExtension("1234567");
  }

  private void canNotUpdatePhoneNumber(String newPhoneNumber, MessageCode messageCode) {
    canNotUpdate(newPhoneNumber, messageCode, service::validatePhoneNumber);
  }

  private void canUpdatePhoneNumber(String newPhoneNumber){
    canUpdate(newPhoneNumber, service::validatePhoneNumber);
  }

  private void canNotUpdatePhoneExtension(String newPhoneExtension, MessageCode messageCode) {
    canNotUpdate(newPhoneExtension, messageCode, service::validatePhoneExtension);
  }

  private void canUpdatePhoneExtension(String newPhoneExtension){
    canUpdate(newPhoneExtension, service::validatePhoneExtension);
  }

  private <T> void canNotUpdate(T validatedValue , MessageCode messageCode, Consumer<T> validateMethod) {
    try {
      validateMethod.accept(validatedValue);
      fail("UserValidationException should be thrown");
    } catch (UserValidationException e) {
      assertThat(e.getErrorCode(), is(messageCode));
    }
  }

  private <T> void canUpdate(T validatedValue, Consumer<T> validateMethod) {
    try {
      validateMethod.accept(validatedValue);
    } catch (UserValidationException e) {
      fail("UserValidationException should not be thrown");
    }
  }

  private void testAdminCanUpdate(UniversalUserToken admin, UserUpdate userUpdate) {
    validateUpdate(admin, userUpdate);
  }

  private void testAdminCanNotUpdate(UniversalUserToken admin, UserUpdate userUpdate) {
    expectedException.expect(UserValidationException.class);
    validateUpdate(admin, userUpdate);
  }

  private void validateUpdate(UniversalUserToken admin, UserUpdate userUpdate) {
    PowerMockito.when(getCurrentUser()).thenReturn(admin);
    User user = new User();
    user.setId(USER_ID);
    service.validateUserUpdate(user, userUpdate);
  }

  private void testAdminCanCreate(UniversalUserToken admin, User user) {
    validateCreate(admin, user);
  }

  private void testAdminCanNotCreate(UniversalUserToken admin, User user) {
    expectedException.expect(UserValidationException.class);
    validateCreate(admin, user);
  }

  private void validateCreate(UniversalUserToken admin, User user) {
    PowerMockito.when(getCurrentUser()).thenReturn(admin);
    service.validateUserCreate(user);
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
