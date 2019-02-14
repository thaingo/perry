package gov.ca.cwds.idm.service.mapper;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.util.Utils.fromDate;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class NsUserMapperTest {

  private NsUserMapper mapper;

  @Before
  public void before() {
    mapper = new NsUserMapper();
  }

  @Test
  public void testtoNsUserNull() {
    assertThat(mapper.toNsUser(null), nullValue());
  }

  @Test
  public void testToNsUser() {
    assertThat(mapper.toNsUser(null), nullValue());

    final String USER_ID = "id";
    final String RACFID = "racfid";
    final String NOTES = "notes";
    final String PHONE_NUMBER = "1234567890";
    final String PHONE_EXTENSION = "48";
    final String FIRST_NAME = "John";
    final String LAST_NAME = "Smith";
    final Date LAST_MODIFIED_TIME = new Date(1000000);
    final Set<String> ROLES = toSet(OFFICE_ADMIN, CWS_WORKER);
    final Set<String> PERMISSIONS = toSet("RFA-rollout", "Hotline-rollout");

    User user = new User();
    user.setId(USER_ID);
    user.setRacfid(RACFID);
    user.setNotes(NOTES);
    user.setPhoneNumber(PHONE_NUMBER);
    user.setPhoneExtensionNumber(PHONE_EXTENSION);
    user.setFirstName(FIRST_NAME);
    user.setLastName(LAST_NAME);
    user.setUserLastModifiedDate(LAST_MODIFIED_TIME);
    user.setRoles(ROLES);
    user.setPermissions(PERMISSIONS);

    NsUser nsUser = mapper.toNsUser(user);

    assertThat(nsUser.getUsername(), is(USER_ID));
    assertThat(nsUser.getRacfid(), is(RACFID));
    assertThat(nsUser.getNotes(), is(NOTES));
    assertThat(nsUser.getPhoneNumber(), is(PHONE_NUMBER));
    assertThat(nsUser.getPhoneExtensionNumber(), is(PHONE_EXTENSION));
    assertThat(nsUser.getFirstName(), is(FIRST_NAME));
    assertThat(nsUser.getLastName(), is(LAST_NAME));
    assertThat(nsUser.getLastModifiedTime(), is(fromDate(LAST_MODIFIED_TIME)));

    Set<String> roles = nsUser.getRoles();
    assertThat(roles, notNullValue());
    assertThat(roles, equalTo(ROLES));

    Set<String> permissions = nsUser.getPermissions();
    assertThat(permissions, notNullValue());
    assertThat(permissions, equalTo(PERMISSIONS));
  }
}
