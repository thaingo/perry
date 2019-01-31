package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.DatabaseUserAttribute.NOTES;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.diff.Diff;
import java.util.Map;
import org.junit.Test;

public class DatabaseDiffMapBuilderTest {

  @Test
  public void testNoChanges() {
    User existedUser = new User();
    existedUser.setNotes("Old Notes");

    UserUpdate updateUserDto = new UserUpdate();

    Map<UserAttribute, Diff> diffMap =
        new DatabaseDiffMapBuilder(existedUser, updateUserDto).build();
    assertTrue(diffMap.isEmpty());

  }

  @Test
  public void testAllChanged() {
    User existedUser = new User();
    existedUser.setNotes("Old Notes");

    UserUpdate updateUserDto = new UserUpdate();
    updateUserDto.setNotes("New Notes");

    Map<UserAttribute, Diff> diffMap =
        new DatabaseDiffMapBuilder(existedUser, updateUserDto).build();

    assertThat(diffMap.size(), is(1));
    assertThat(diffMap.get(NOTES).getOldValue(), is("Old Notes"));
    assertThat(diffMap.get(NOTES).getNewValue(), is("New Notes"));
  }
}
