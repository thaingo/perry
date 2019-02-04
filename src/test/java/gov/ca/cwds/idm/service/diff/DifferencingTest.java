package gov.ca.cwds.idm.service.diff;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;

public class DifferencingTest {

  private static final String EXISTED_EMAIL = "user@OCI.CA.GOV";
  private static final String NEW_EMAIL = "NEW@e.mail";

  private static final Boolean EXISTED_ENABLED = Boolean.TRUE;
  private static final Boolean NEW_ENABLED = !EXISTED_ENABLED;

  private static final String EXISTED_PHONE = "1234567890";
  private static final String NEW_PHONE ="9874563210";

  private static final String EXISTED_PHONE_EXTENSION = "28";
  private static final String NEW_PHONE_EXTENSION = "333";

  private static final String EXISTED_NOTES = "old notes";
  private static final String NEW_NOTES = "new notes";

  private static final HashSet<String> EXISTED_ROLES = toSet(STATE_ADMIN, COUNTY_ADMIN);
  private static final HashSet<String> NEW_ROLES = toSet(COUNTY_ADMIN, OFFICE_ADMIN);

  private static final HashSet<String> EXISTED_PERMISSIONS =
      toSet("Snapshot-rollout", "Hotline-rollout");
  private static final HashSet<String> NEW_PERMISSIONS
      = toSet("Hotline-rollout", "CANS-rollout");

  @Test
  public void testNoChanges() {
    Differencing differencing = new Differencing(existedUser(), new UserUpdate());
    assertNoDiffs(differencing);
  }

  @Test
  public void testNewAreTheSame() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail(EXISTED_EMAIL.toLowerCase());
    userUpdate.setEnabled(EXISTED_ENABLED);
    userUpdate.setPhoneNumber(EXISTED_PHONE);
    userUpdate.setPhoneExtensionNumber(EXISTED_PHONE_EXTENSION);
    userUpdate.setNotes(EXISTED_NOTES);
    userUpdate.setRoles(EXISTED_ROLES);
    userUpdate.setPermissions(EXISTED_PERMISSIONS);

    Differencing differencing = new Differencing(existedUser(), userUpdate);
    assertNoDiffs(differencing);
  }

  @Test
  public void testAllChanged() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail(NEW_EMAIL);
    userUpdate.setEnabled(NEW_ENABLED);
    userUpdate.setPhoneNumber(NEW_PHONE);
    userUpdate.setPhoneExtensionNumber(NEW_PHONE_EXTENSION);
    userUpdate.setNotes(NEW_NOTES);
    userUpdate.setRoles(NEW_ROLES);
    userUpdate.setPermissions(NEW_PERMISSIONS);

    Differencing differencing = new Differencing(existedUser(), userUpdate);

    assertStringDiff(differencing.getEmailDiff(), EXISTED_EMAIL.toLowerCase(), NEW_EMAIL.toLowerCase());
    assertBooleanDiff(differencing.getEnabledDiff(), EXISTED_ENABLED, NEW_ENABLED);
    assertStringDiff(differencing.getPhoneNumberDiff(), EXISTED_PHONE, NEW_PHONE);
    assertStringDiff(
        differencing.getPhoneExtensionNumberDiff(), EXISTED_PHONE_EXTENSION, NEW_PHONE_EXTENSION);
    assertStringDiff(differencing.getNotesDiff(), EXISTED_NOTES, NEW_NOTES);
    assertStringSetDiff(differencing.getRolesDiff(), EXISTED_ROLES, NEW_ROLES);
    assertStringSetDiff(differencing.getPermissionsDiff(), EXISTED_PERMISSIONS, NEW_PERMISSIONS);
  }

  private void assertStringDiff(Optional<StringDiff> optDiff, String expectedOldValue,
      String expectedNewValue) {
    assertTrue(optDiff.isPresent());
    StringDiff diff = optDiff.get();
    assertThat(diff.getOldValue(), is(expectedOldValue));
    assertThat(diff.getNewValue(), is(expectedNewValue));
  }

  private void assertBooleanDiff(Optional<BooleanDiff> optDiff, Boolean expectedOldValue,
      Boolean expectedNewValue) {
    assertTrue(optDiff.isPresent());
    BooleanDiff diff = optDiff.get();
    assertThat(diff.getOldValue(), is(expectedOldValue));
    assertThat(diff.getNewValue(), is(expectedNewValue));
  }

  private void assertStringSetDiff(Optional<StringSetDiff> optDiff, Set<String> expectedOldValue,
      Set<String> expectedNewValue) {
    assertTrue(optDiff.isPresent());
    StringSetDiff diff = optDiff.get();
    assertThat(diff.getOldValue(), is(expectedOldValue));
    assertThat(diff.getNewValue(), is(expectedNewValue));
  }

  private void assertNoDiffs(Differencing differencing) {
    assertFalse(differencing.getEmailDiff().isPresent());
    assertFalse(differencing.getEnabledDiff().isPresent());
    assertFalse(differencing.getPhoneNumberDiff().isPresent());
    assertFalse(differencing.getPhoneExtensionNumberDiff().isPresent());
    assertFalse(differencing.getNotesDiff().isPresent());
    assertFalse(differencing.getRolesDiff().isPresent());
    assertFalse(differencing.getPermissionsDiff().isPresent());
  }

  private static User existedUser() {
    User user = new User();
    user.setEmail(EXISTED_EMAIL);
    user.setEnabled(EXISTED_ENABLED);
    user.setPhoneNumber(EXISTED_PHONE);
    user.setPhoneExtensionNumber(EXISTED_PHONE_EXTENSION);
    user.setNotes(EXISTED_NOTES);
    user.setRoles(EXISTED_ROLES);
    user.setPermissions(EXISTED_PERMISSIONS);
    return user;
  }
}
