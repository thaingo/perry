package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
import java.util.Set;
import org.junit.Test;

public class NsUserBuilderTest {

  @Test
  public void testAllChanged() {
    final String OLD_PHONE_NUMBER = "1234567890";
    final String OLD_PHONE_EXTENSION = "11";
    final String OLD_NOTES = "Old Notes";
    final Set<String> OLD_ROLES = toSet(STATE_ADMIN, OFFICE_ADMIN);
    final Set<String> OLD_PERMISSIONS = toSet();

    final String NEW_PHONE_NUMBER = "0987654321";
    final String NEW_PHONE_EXTENSION = "99";
    final String NEW_NOTES = "New Notes";
    final Set<String> NEW_ROLES = toSet(COUNTY_ADMIN, OFFICE_ADMIN);
    final Set<String> NEW_PERMISSIONS = toSet("Hotline-rollout");

    User existedUser = new User();
    existedUser.setPhoneNumber(OLD_PHONE_NUMBER);
    existedUser.setPhoneExtensionNumber(OLD_PHONE_EXTENSION);
    existedUser.setNotes(OLD_NOTES);
    existedUser.setRoles(OLD_ROLES);
    existedUser.setPermissions(OLD_PERMISSIONS);

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setPhoneNumber(NEW_PHONE_NUMBER);
    userUpdate.setPhoneExtensionNumber(NEW_PHONE_EXTENSION);
    userUpdate.setNotes(NEW_NOTES);
    userUpdate.setRoles(NEW_ROLES);
    userUpdate.setPermissions(NEW_PERMISSIONS);

    UpdateDifference updateDifference = new UpdateDifference(existedUser, userUpdate);

    NsUser existedNsUser = new NsUser();
    existedNsUser.setNotes(OLD_NOTES);
    existedNsUser.setPhoneNumber(OLD_PHONE_NUMBER);
    existedNsUser.setPhoneExtensionNumber(OLD_PHONE_EXTENSION);
    existedNsUser.setRoles(OLD_ROLES);
    existedNsUser.setPermissions(OLD_PERMISSIONS);

    NsUserBuilder nsUserBuilder = new NsUserBuilder(existedNsUser, updateDifference);
    NsUser modifiedNsUser = nsUserBuilder.build();

    assertTrue(nsUserBuilder.userIsUpdated());

    assertThat(modifiedNsUser.getPhoneNumber(), is(NEW_PHONE_NUMBER));
    assertThat(modifiedNsUser.getPhoneExtensionNumber(), is(NEW_PHONE_EXTENSION));
    assertThat(modifiedNsUser.getNotes(), is(NEW_NOTES));
    assertThat(modifiedNsUser.getRoles(), is(NEW_ROLES));
    assertThat(modifiedNsUser.getPermissions(), is(NEW_PERMISSIONS));
  }

  @Test
  public void testNoChanges() {
    final String OLD_PHONE_NUMBER = "1234567890";
    final String OLD_PHONE_EXTENSION = "11";
    final String OLD_NOTES = "Old Notes";
    final Set<String> OLD_ROLES = toSet(STATE_ADMIN, OFFICE_ADMIN);
    final Set<String> OLD_PERMISSIONS = toSet();

    User existedUser = new User();
    existedUser.setPhoneNumber(OLD_PHONE_NUMBER);
    existedUser.setPhoneExtensionNumber(OLD_PHONE_EXTENSION);
    existedUser.setNotes(OLD_NOTES);
    existedUser.setRoles(OLD_ROLES);
    existedUser.setPermissions(OLD_PERMISSIONS);

    UpdateDifference updateDifference = new UpdateDifference(existedUser, new UserUpdate());

    NsUser existedNsUser = new NsUser();
    existedNsUser.setNotes(OLD_NOTES);
    existedNsUser.setPhoneNumber(OLD_PHONE_NUMBER);
    existedNsUser.setPhoneExtensionNumber(OLD_PHONE_EXTENSION);
    existedNsUser.setRoles(OLD_ROLES);
    existedNsUser.setPermissions(OLD_PERMISSIONS);

    NsUserBuilder nsUserBuilder = new NsUserBuilder(existedNsUser, updateDifference);
    NsUser modifiedNsUser = nsUserBuilder.build();

    assertFalse(nsUserBuilder.userIsUpdated());

    assertThat(modifiedNsUser.getPhoneNumber(), is(OLD_PHONE_NUMBER));
    assertThat(modifiedNsUser.getPhoneExtensionNumber(), is(OLD_PHONE_EXTENSION));
    assertThat(modifiedNsUser.getNotes(), is(OLD_NOTES));
    assertThat(modifiedNsUser.getRoles(), is(OLD_ROLES));
    assertThat(modifiedNsUser.getPermissions(), is(OLD_PERMISSIONS));
  }
}
