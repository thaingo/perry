package gov.ca.cwds.service.scripts;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.COUNTY;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.IS_LOCKED;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.OFFICE;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.util.TestUtils.attr;
import static gov.ca.cwds.idm.util.TestUtils.dateTime;
import static gov.ca.cwds.idm.util.TestUtils.toMillis;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.cognitoidp.model.UserStatusType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class IdmMappingScriptTest {

  static String NS_USER_USERNAME = "NS_USER_USERNAME";
  static String NS_USER_RACFID = "NS_USER_RACFID";
  static String NS_USER_NOTES = "NS_USER_NOTES";
  static String NS_USER_PHONE_NUMBER = "1234567890";
  static String NS_USER_PHONE_EXTENSION_NUMBER = "678";
  static long NS_USER_LAST_LOGIN_TIME_MILLIS = 110000000L;
  static LocalDateTime NS_USER_LAST_LOGIN_TIME = dateTime(NS_USER_LAST_LOGIN_TIME_MILLIS);
  static Set<String> NS_USER_ROLES = toSet(CWS_WORKER, COUNTY_ADMIN);
  static Set<String> NS_USER_PERMISSIONS = toSet("Hotline-rollout", "RFA-rollout");
  static long NS_USER_LAST_MODIFIED_TIME_MILLIS = 130000000L;
  static String NS_USER_FIRST_NAME = "NS_USER_FIRST_NAME";
  static String NS_USER_LAST_NAME = "NS_USER_FIRST_NAME";

  static long IDM_USER_LAST_MODIFIED_TIME_MILLIS = 120000000L;
  static Boolean IDM_USER_ENABLED = Boolean.TRUE;
  static long IDM_USER_CREATED_TIME_MILLIS = 100000000L;
  static UserStatusType IDM_USER_STATUS = UserStatusType.CONFIRMED;
  static String IDM_USER_EMAIL = "idm.cognito@gmail.com";
  static String IDM_LOCKED = "false";
  static String IDM_COUNTY_NAME = "IDM_COUNTY_NAME";
  static String IDM_OFFICE_ID = "IDM_OFFICE_ID";


  private IdmMappingScript idpMappingScript;

  @Before
  public void before() throws Exception {
    String path = Paths.get(getClass().getResource("/scripts/cognito/idm.groovy").toURI()).toString();
    idpMappingScript = new IdmMappingScript(path);
  }

  @Test
  public void testNonRacfidUser() throws Exception {

    User user = idpMappingScript.map(cognitoUser(), null, nonRacfidNsUser());

    assertThat(user, notNullValue());
    assertThat(user.getId(), is(NS_USER_USERNAME));
    assertThat(user.getRacfid(), is(nullValue()));
    assertThat(user.getPhoneNumber(), is(NS_USER_PHONE_NUMBER));
    assertThat(user.getPhoneExtensionNumber(), is(NS_USER_PHONE_EXTENSION_NUMBER));
    assertThat(toMillis(user.getLastLoginDateTime()), is(NS_USER_LAST_LOGIN_TIME_MILLIS));
    assertThat(user.getNotes(), is(NS_USER_NOTES));
    assertThat(user.getRoles(), is(NS_USER_ROLES));
    assertThat(user.getPermissions(), is(NS_USER_PERMISSIONS));

    assertThat(user.getUserLastModifiedDate().getTime(), is(NS_USER_LAST_MODIFIED_TIME_MILLIS));

    assertThat(user.getEnabled(), is(IDM_USER_ENABLED));
    assertThat(user.getUserCreateDate().getTime(), is(IDM_USER_CREATED_TIME_MILLIS));
    assertThat(user.getStatus(), is(IDM_USER_STATUS.toString()));
    assertThat(user.getEmail(), is(IDM_USER_EMAIL));
    assertThat(user.isLocked(), is(false));
    assertThat(user.getFirstName(), is(NS_USER_FIRST_NAME));
    assertThat(user.getLastName(), is(NS_USER_LAST_NAME));
    assertThat(user.getCountyName(), is(IDM_COUNTY_NAME));
    assertThat(user.getOfficeId(), is(IDM_OFFICE_ID));
  }

  @Test
  public void testModifiedDatesAreNull() throws Exception {
    testUserLastModifiedDate(null, null,null);
  }

  @Test
  public void testCognitoModifiedDateIsNull() throws Exception {
    final long nsDbModifiedDateMillis = 1000000;

    testUserLastModifiedDate(null, dateTime(nsDbModifiedDateMillis),
        new Date(nsDbModifiedDateMillis));
  }

  @Test
  public void testNsModifiedDateIsNull() throws Exception {
    final Date cognitoModifiedDate = new Date(1000000);
    testUserLastModifiedDate(cognitoModifiedDate, null, cognitoModifiedDate);
  }

  @Test
  public void testCognitoModifiedDateIsGreater() throws Exception {
    final long cognitoModifiedDateMillis = 2000000;
    final long nsDbModifiedDateMillis = 1000000;

    final Date cognitoModifiedDate = new Date(cognitoModifiedDateMillis);
    final LocalDateTime nsDbModifiedDate  = dateTime(nsDbModifiedDateMillis);

    testUserLastModifiedDate(cognitoModifiedDate, nsDbModifiedDate, cognitoModifiedDate);
  }

  @Test
  public void testNsModifiedDateIsGreater() throws Exception {
    final long cognitoModifiedDateMillis = 1000000;
    final long nsDbModifiedDateMillis = 2000000;

    final Date cognitoModifiedDate = new Date(cognitoModifiedDateMillis);
    final LocalDateTime nsDbModifiedDate  = dateTime(nsDbModifiedDateMillis);

    testUserLastModifiedDate(cognitoModifiedDate, nsDbModifiedDate, new Date(nsDbModifiedDateMillis));
  }

  private void testUserLastModifiedDate(
       Date cognitoTime, LocalDateTime nsUserTime, Date expectedModifiedDate) throws Exception {

    UserType cognitoUser = cognitoUser();
    cognitoUser.setUserLastModifiedDate(cognitoTime);

    NsUser nsUser = nonRacfidNsUser();
    nsUser.setLastModifiedTime(nsUserTime);

    User user = idpMappingScript.map(cognitoUser, null, nsUser);
    assertThat(user.getUserLastModifiedDate(), is(expectedModifiedDate));
  }

  private static NsUser nsUser() {
    NsUser nsUser = new NsUser();
    nsUser.setUsername(NS_USER_USERNAME);
    nsUser.setRacfid(NS_USER_RACFID);
    nsUser.setPhoneNumber(NS_USER_PHONE_NUMBER);
    nsUser.setPhoneExtensionNumber(NS_USER_PHONE_EXTENSION_NUMBER);
    nsUser.setLastLoginTime(NS_USER_LAST_LOGIN_TIME);
    nsUser.setNotes(NS_USER_NOTES);
    nsUser.setRoles(NS_USER_ROLES);
    nsUser.setPermissions(NS_USER_PERMISSIONS);
    nsUser.setLastModifiedTime(dateTime(NS_USER_LAST_MODIFIED_TIME_MILLIS));
    nsUser.setFirstName(NS_USER_FIRST_NAME);
    nsUser.setLastName(NS_USER_LAST_NAME);
    return nsUser;
  }

  private static NsUser nonRacfidNsUser() {
    NsUser nsUser = nsUser();
    nsUser.setRacfid(null);
    return nsUser;
  }

  private static UserType cognitoUser() {
    UserType cognitoUser = new UserType();
    cognitoUser.setUserLastModifiedDate(new Date(IDM_USER_LAST_MODIFIED_TIME_MILLIS));
    cognitoUser.setEnabled(IDM_USER_ENABLED);
    cognitoUser.setUserCreateDate(new Date(IDM_USER_CREATED_TIME_MILLIS));
    cognitoUser.setUserStatus(UserStatusType.CONFIRMED);
    cognitoUser.withAttributes(
        attr(EMAIL, IDM_USER_EMAIL),
        attr(IS_LOCKED, IDM_LOCKED),
        attr(COUNTY, IDM_COUNTY_NAME),
        attr(OFFICE, IDM_OFFICE_ID)
    );
    return cognitoUser;
  }
}
