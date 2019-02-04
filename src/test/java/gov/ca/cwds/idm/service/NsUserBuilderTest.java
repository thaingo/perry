package gov.ca.cwds.idm.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.diff.Differencing;
import org.junit.Test;

public class NsUserBuilderTest {

  @Test
  public void testAllChanged() {
    User existedUser = new User();
    existedUser.setNotes("Old Notes");

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setNotes("New Notes");

    Differencing differencing = new Differencing(existedUser, userUpdate);

    NsUser existedNsUser = new NsUser();
    existedNsUser.setNotes("Old Notes");

    NsUserBuilder nsUserBuilder = new NsUserBuilder(existedNsUser, differencing);
    NsUser modifiedNsUser = nsUserBuilder.build();
    assertTrue(nsUserBuilder.userIsUpdated());
    assertThat(modifiedNsUser.getNotes(), is("New Notes"));
  }

  @Test
  public void testNoChanges() {
    User existedUser = new User();
    existedUser.setNotes("Old Notes");
    Differencing differencing = new Differencing(existedUser, new UserUpdate());

    NsUser existedNsUser = new NsUser();
    existedNsUser.setNotes("Old Notes");

    NsUserBuilder nsUserBuilder = new NsUserBuilder(existedNsUser, differencing);
    NsUser modifiedNsUser = nsUserBuilder.build();
    assertFalse(nsUserBuilder.userIsUpdated());
    assertThat(modifiedNsUser.getNotes(), is("Old Notes"));
  }
}
