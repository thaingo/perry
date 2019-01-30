package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.cognito.attribute.DatabaseUserAttribute.NOTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.NsUserService.NsUserBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.diff.Diff;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class NsUserBuilderTest {

  @Test
  public void testAllChanged() {
    NsUser existedUser = new NsUser();
    existedUser.setNotes("Old Notes");

    Map<UserAttribute, Diff> databaseDiffMap = new HashMap<>();
    databaseDiffMap.put(NOTES, new Diff<>( "Old Notes","New Notes"));

    NsUser modifiedNsUser = new NsUserBuilder(existedUser, databaseDiffMap).build();
    assertThat(modifiedNsUser.getNotes(), is("New Notes"));
  }

  @Test
  public void testNoChanges() {
    NsUser existedUser = new NsUser();
    existedUser.setNotes("Old Notes");

    Map<UserAttribute, Diff> databaseDiffMap = new HashMap<>();

    NsUser modifiedNsUser = new NsUserBuilder(existedUser, databaseDiffMap).build();
    assertThat(modifiedNsUser.getNotes(), is("Old Notes"));
  }
}
