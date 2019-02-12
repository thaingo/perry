package gov.ca.cwds.idm.service.mapper;

import static gov.ca.cwds.util.Utils.fromDate;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import java.util.Date;
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

    User user = new User();
    user.setId(USER_ID);
    user.setRacfid(RACFID);
    user.setNotes(NOTES);
    user.setPhoneNumber(PHONE_NUMBER);
    user.setPhoneExtensionNumber(PHONE_EXTENSION);
    user.setFirstName(FIRST_NAME);
    user.setLastName(LAST_NAME);
    user.setUserLastModifiedDate(LAST_MODIFIED_TIME);

    NsUser nsUser = mapper.toNsUser(user);

    assertThat(nsUser.getUsername(), is(USER_ID));
    assertThat(nsUser.getRacfid(), is(RACFID));
    assertThat(nsUser.getNotes(), is(NOTES));
    assertThat(nsUser.getPhoneNumber(), is(PHONE_NUMBER));
    assertThat(nsUser.getPhoneExtensionNumber(), is(PHONE_EXTENSION));
    assertThat(nsUser.getFirstName(), is(FIRST_NAME));
    assertThat(nsUser.getLastName(), is(LAST_NAME));
    assertThat(nsUser.getLastModifiedTime(), is(fromDate(LAST_MODIFIED_TIME)));
  }
}
