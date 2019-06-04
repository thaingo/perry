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

public class UpdateDifferenceTest {

  private static final String EXISTED_EMAIL = "old.user@oci.ca.gov";
  private static final String NEW_EMAIL = "new.user@oci.ca.gov";

  private static final Boolean EXISTED_ENABLED = Boolean.TRUE;
  private static final Boolean NEW_ENABLED = !EXISTED_ENABLED;

  private static final String EXISTED_PHONE = "1234567890";
  private static final String NEW_PHONE ="9874563210";

  private static final String EXISTED_PHONE_EXTENSION = "28";
  private static final String NEW_PHONE_EXTENSION = "333";

  private static final String EXISTED_CELL_PHONE = "1112223333";
  private static final String NEW_CELL_PHONE ="4445556666";

  private static final String EXISTED_NOTES = "old notes";
  private static final String NEW_NOTES = "new notes";

  private static final HashSet<String> EXISTED_ROLES = toSet(STATE_ADMIN, COUNTY_ADMIN);
  private static final HashSet<String> NEW_ROLES = toSet(COUNTY_ADMIN, OFFICE_ADMIN);

  private static final HashSet<String> EXISTED_PERMISSIONS =
      toSet("Snapshot-rollout", "Hotline-rollout");
  private static final HashSet<String> NEW_PERMISSIONS
      = toSet("Hotline-rollout", "CANS-rollout");

  final String UPPER_CASE_EMAIL = "SOME.USER@EMAIL";
  final String LOWER_CASE_EMAIL = "some.user@email";

  @Test
  public void testNoChanges() {
    UpdateDifference updateDifference = new UpdateDifference(existedUser(), new UserUpdate());
    assertNoDiffs(updateDifference);
  }

  @Test
  public void testNewAreTheSame() {
    User user = existedUser();

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail(EXISTED_EMAIL);
    userUpdate.setEnabled(EXISTED_ENABLED);
    userUpdate.setPhoneNumber(EXISTED_PHONE);
    userUpdate.setPhoneExtensionNumber(EXISTED_PHONE_EXTENSION);
    userUpdate.setNotes(EXISTED_NOTES);
    userUpdate.setRoles(EXISTED_ROLES);
    userUpdate.setPermissions(EXISTED_PERMISSIONS);

    UpdateDifference updateDifference = new UpdateDifference(user, userUpdate);
    assertNoDiffs(updateDifference);
  }

  @Test
  public void testChangeEmailCaseToLower() {
    User user = existedUser();
    UserUpdate userUpdate = new UserUpdate();

    user.setEmail(UPPER_CASE_EMAIL);
    userUpdate.setEmail(UPPER_CASE_EMAIL);
    UpdateDifference updateDifference = new UpdateDifference(user, userUpdate);
    assertStringDiff(updateDifference.getEmailDiff(), UPPER_CASE_EMAIL, LOWER_CASE_EMAIL);

    user.setEmail(UPPER_CASE_EMAIL);
    userUpdate.setEmail(LOWER_CASE_EMAIL);
    updateDifference = new UpdateDifference(user, userUpdate);
    assertStringDiff(updateDifference.getEmailDiff(), UPPER_CASE_EMAIL, LOWER_CASE_EMAIL);
  }

  @Test
  public void testCanNotChangeEmailCaseToUpper() {
    User user = existedUser();
    UserUpdate userUpdate = new UserUpdate();

    user.setEmail(LOWER_CASE_EMAIL);
    userUpdate.setEmail(UPPER_CASE_EMAIL);
    UpdateDifference updateDifference = new UpdateDifference(user, userUpdate);
    assertNoDiffs(updateDifference);
  }

  @Test
  public void testAllChanged() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail(NEW_EMAIL);
    userUpdate.setEnabled(NEW_ENABLED);
    userUpdate.setPhoneNumber(NEW_PHONE);
    userUpdate.setPhoneExtensionNumber(NEW_PHONE_EXTENSION);
      userUpdate.setCellPhoneNumber(NEW_CELL_PHONE);
    userUpdate.setNotes(NEW_NOTES);
    userUpdate.setRoles(NEW_ROLES);
    userUpdate.setPermissions(NEW_PERMISSIONS);

    UpdateDifference updateDifference = new UpdateDifference(existedUser(), userUpdate);

    assertStringDiff(updateDifference.getEmailDiff(), EXISTED_EMAIL, NEW_EMAIL.toLowerCase());
    assertBooleanDiff(updateDifference.getEnabledDiff(), EXISTED_ENABLED, NEW_ENABLED);
    assertStringDiff(updateDifference.getPhoneNumberDiff(), EXISTED_PHONE, NEW_PHONE);
    assertStringDiff(
        updateDifference.getPhoneExtensionNumberDiff(), EXISTED_PHONE_EXTENSION, NEW_PHONE_EXTENSION);
      assertStringDiff(updateDifference.getCellPhoneNumberDiff(), EXISTED_CELL_PHONE, NEW_CELL_PHONE);
    assertStringDiff(updateDifference.getNotesDiff(), EXISTED_NOTES, NEW_NOTES);
    assertStringSetDiff(updateDifference.getRolesDiff(), EXISTED_ROLES, NEW_ROLES);
    assertStringSetDiff(updateDifference.getPermissionsDiff(), EXISTED_PERMISSIONS, NEW_PERMISSIONS);
  }

  @Test
  public void testBlankStrings() {
    assertNoCellPhoneDiff(null, null);
    assertNoCellPhoneDiff(null, "");
    assertNoCellPhoneDiff(null, " ");

    assertCellPhoneDiff("", null, null);
    assertCellPhoneDiff(" ", null, null);
    assertCellPhoneDiff("12234567890", "", null);
    assertCellPhoneDiff("12234567890", " ", null);
    assertCellPhoneDiff(" ", "", null);
    assertCellPhoneDiff("", "", null);
    assertCellPhoneDiff(" ", " ", null);
  }

  private void assertNoCellPhoneDiff(String inputOldValue, String inputNewValue) {
    User user = new User();
    user.setCellPhoneNumber(inputOldValue);
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setCellPhoneNumber(inputNewValue);
    UpdateDifference updateDifference = new UpdateDifference(user, userUpdate);
    assertFalse(updateDifference.getCellPhoneNumberDiff().isPresent());
  }

  private void assertCellPhoneDiff(String inputOldValue, String inputNewValue,
      String expectedOutputNewValue) {
    User user = new User();
    user.setCellPhoneNumber(inputOldValue);

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setCellPhoneNumber(inputNewValue);

    UpdateDifference updateDifference = new UpdateDifference(user, userUpdate);

    assertStringDiff(updateDifference.getCellPhoneNumberDiff(), inputOldValue,
        expectedOutputNewValue);
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

  private void assertNoDiffs(UpdateDifference updateDifference) {
    assertFalse(updateDifference.getEmailDiff().isPresent());
    assertFalse(updateDifference.getEnabledDiff().isPresent());
    assertFalse(updateDifference.getPhoneNumberDiff().isPresent());
    assertFalse(updateDifference.getPhoneExtensionNumberDiff().isPresent());
    assertFalse(updateDifference.getNotesDiff().isPresent());
    assertFalse(updateDifference.getRolesDiff().isPresent());
    assertFalse(updateDifference.getPermissionsDiff().isPresent());
  }

  private static User existedUser() {
    User user = new User();
    user.setEmail(EXISTED_EMAIL);
    user.setEnabled(EXISTED_ENABLED);
    user.setPhoneNumber(EXISTED_PHONE);
    user.setPhoneExtensionNumber(EXISTED_PHONE_EXTENSION);
    user.setCellPhoneNumber(EXISTED_CELL_PHONE);
    user.setNotes(EXISTED_NOTES);
    user.setRoles(EXISTED_ROLES);
    user.setPermissions(EXISTED_PERMISSIONS);
    return user;
  }
}
