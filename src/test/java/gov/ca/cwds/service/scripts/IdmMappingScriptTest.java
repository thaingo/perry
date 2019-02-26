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
import static gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType.MADERA;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.amazonaws.services.cognitoidp.model.UserStatusType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.idm.dto.CwsStaffPrivilege;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType;
import gov.ca.cwds.service.dto.CwsUserInfo;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class IdmMappingScriptTest {

  private static final String NS_USER_USERNAME = "NS_USER_USERNAME";
  private static final String NS_USER_RACFID = "NS_USER_RACFID";
  private static final String NS_USER_NOTES = "NS_USER_NOTES";
  private static final String NS_USER_PHONE_NUMBER = "1234567890";
  private static final String NS_USER_PHONE_EXTENSION_NUMBER = "678";
  private static final long NS_USER_LAST_LOGIN_TIME_MILLIS = 110000000L;
  private static final LocalDateTime NS_USER_LAST_LOGIN_TIME = dateTime(NS_USER_LAST_LOGIN_TIME_MILLIS);
  private static final Set<String> NS_USER_ROLES = toSet(CWS_WORKER, COUNTY_ADMIN);
  private static final Set<String> NS_USER_PERMISSIONS = toSet("Hotline-rollout", "RFA-rollout");
  private static final long NS_USER_LAST_MODIFIED_TIME_MILLIS = 130000000L;
  private static final String NS_USER_FIRST_NAME = "NS_USER_FIRST_NAME";
  private static final String NS_USER_LAST_NAME = "NS_USER_FIRST_NAME";

  private static final long IDM_USER_LAST_MODIFIED_TIME_MILLIS = 120000000L;
  private static final Boolean IDM_USER_ENABLED = Boolean.TRUE;
  private static final long IDM_USER_CREATED_TIME_MILLIS = 100000000L;
  private static final UserStatusType IDM_USER_STATUS = UserStatusType.CONFIRMED;
  private static final String IDM_USER_EMAIL = "idm.cognito@gmail.com";
  private static final String IDM_LOCKED = "false";
  private static final String IDM_COUNTY_NAME = "IDM_COUNTY_NAME";
  private static final String IDM_OFFICE_ID = "IDM_OFFICE_ID";

  private static final String CWS_FIRST_NAME = "CWS_FIRST_NAME";
  private static final String CWS_LAST_NAME = "CWS_LAST_NAME";
  private static final GovernmentEntityType CWS_COUNTY = MADERA;
  private static final String CWS_OFFICE_ID = "CWS_OFFICE_ID";
  private static final LocalDate CWS_USER_START_DATE = LocalDate.of(2007, 4, 25);
  private static final Long CWS_OFFICE_PHONE_NUMBER = 9876543210L;
  private static final String CWS_OFFICE_PHONE_EXTENSION = "987";
  private static final String CWS_STAFF_PRIVILEGE_CATEGORY = "CWS_STAFF_PRIVILEGE_CATEGORY";
  private static final String CWS_STAFF_PRIVILEGE_DESC = "CWS_STAFF_PRIVILEGE_DESC";

  private IdmMappingScript idpMappingScript;

  @Before
  public void before() throws Exception {
    String path = Paths.get(getClass().getResource("/scripts/cognito/idm.groovy").toURI()).toString();
    idpMappingScript = new IdmMappingScript(path);
  }

  @Test
  public void testRacfidUser() throws Exception {
    CwsUserInfo cwsUser = cwsUser();

    User user = idpMappingScript.map(cognitoUser(), cwsUser, nsUser());

    assertThat(user, notNullValue());
    assertThat(user.getId(), is(NS_USER_USERNAME));
    assertThat(user.getRacfid(), is(NS_USER_RACFID));
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

    assertThat(user.getFirstName(), is(CWS_FIRST_NAME));
    assertThat(user.getLastName(), is(CWS_LAST_NAME));
    assertThat(user.getCountyName(), is(CWS_COUNTY.getDescription()));
    assertThat(user.getOfficeId(), is(CWS_OFFICE_ID));
    assertThat(user.getStartDate(), is(CWS_USER_START_DATE));
    assertThat(user.getEndDate(), is(nullValue()));
    assertThat(user.getOfficePhoneNumber(), is(CWS_OFFICE_PHONE_NUMBER.toString()));
    assertThat(user.getOfficePhoneExtensionNumber(), is(CWS_OFFICE_PHONE_EXTENSION));

    Set<CwsStaffPrivilege> cwsStaffPrivileges = user.getCwsPrivileges();
    assertThat(cwsStaffPrivileges.size(), is(1));
    CwsStaffPrivilege cwsStaffPrivilege = cwsStaffPrivileges.iterator().next();
    assertThat(cwsStaffPrivilege.getCategory(), is(CWS_STAFF_PRIVILEGE_CATEGORY));
    assertThat(cwsStaffPrivilege.getPrivilege(), is(CWS_STAFF_PRIVILEGE_DESC));
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
    assertThat(user.getStartDate(), is(nullValue()));
    assertThat(user.getEndDate(), is(nullValue()));
    assertThat(user.getOfficePhoneNumber(), is(nullValue()));
    assertThat(user.getOfficePhoneExtensionNumber(), is(nullValue()));
    assertTrue(user.getCwsPrivileges().isEmpty());
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

  private CwsUserInfo cwsUser() {
    CwsUserInfo cwsUser = new CwsUserInfo();

    StaffPerson staffPerson = new StaffPerson();
    staffPerson.setFirstName(CWS_FIRST_NAME);
    staffPerson.setLastName(CWS_LAST_NAME);
    staffPerson.setStartDate(CWS_USER_START_DATE);
    staffPerson.setEndDate(null);
    cwsUser.setStaffPerson(staffPerson);

    CwsOffice cwsOffice = new CwsOffice();
    cwsOffice.setGovernmentEntityType((short)CWS_COUNTY.getSysId());
    cwsOffice.setOfficeId(CWS_OFFICE_ID);
    cwsOffice.setPrimaryPhoneNumber(CWS_OFFICE_PHONE_NUMBER);
    cwsOffice.setPrimaryPhoneExtensionNumber(CWS_OFFICE_PHONE_EXTENSION);
    cwsUser.setCwsOffice(cwsOffice);

    Set<CwsStaffPrivilege> cwsStaffPrivileges = new HashSet<>();
    CwsStaffPrivilege cwsStaffPrivilege = new CwsStaffPrivilege(CWS_STAFF_PRIVILEGE_CATEGORY,
        CWS_STAFF_PRIVILEGE_DESC);
    cwsStaffPrivileges.add(cwsStaffPrivilege);
    cwsUser.setCwsStaffPrivs(cwsStaffPrivileges);

    return cwsUser;
  }
}
