package gov.ca.cwds.idm.service.mapper;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
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

    User user = new User();
    user.setId(USER_ID);
    user.setRacfid(RACFID);
    user.setNotes(NOTES);
    user.setPhoneNumber(PHONE_NUMBER);

    NsUser nsUser = mapper.toNsUser(user);

    assertThat(nsUser.getUsername(), is(USER_ID));
    assertThat(nsUser.getRacfid(), is(RACFID));
    assertThat(nsUser.getNotes(), is(NOTES));
    assertThat(nsUser.getPhoneNumber(), is(PHONE_NUMBER));
  }
}
