package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.PHONE_NUMBER;
import static gov.ca.cwds.idm.service.cognito.attribute.UserUpdateAttributesUtil.buildUpdatedAttributesList;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
import java.util.List;
import org.junit.Test;

public class UserUpdateAttributesUtilTest {

  @Test
  public void testNoChanges() {
    UpdateDifference updateDifference = new UpdateDifference(existedCognitoUser(), new UserUpdate());
    List<AttributeType> updatedAttributes = buildUpdatedAttributesList(updateDifference);
    assertThat(updatedAttributes.size(), is(0));
  }

  @Test
  public void testTheSameValues() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail("user@oci.ca.gov");
    userUpdate.setPhoneNumber("1234567890");
    userUpdate.setPhoneExtensionNumber("28");
    userUpdate.setCellPhoneNumber("2223334444");
    userUpdate.setRoles(toSet("State-admin", "County-admin"));
    userUpdate.setPermissions(toSet("Snapshot-rollout", "Hotline-rollout"));

    UpdateDifference updateDifference = new UpdateDifference(existedCognitoUser(), userUpdate);
    List<AttributeType> updatedAttributes = buildUpdatedAttributesList(updateDifference);
    assertThat(updatedAttributes.size(), is(0));
  }

  @Test
  public void testDifferentValues() {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail("admin@oci.ca.gov");
    userUpdate.setCellPhoneNumber("5556667777");

    UpdateDifference updateDifference = new UpdateDifference(existedCognitoUser(), userUpdate);
    List<AttributeType> updatedAttributes = buildUpdatedAttributesList(updateDifference);
    assertThat(updatedAttributes.size(), is(3));

    assertAttribute(updatedAttributes.get(0), EMAIL, "admin@oci.ca.gov");
    assertAttribute(updatedAttributes.get(1), EMAIL_VERIFIED, "True");
    assertAttribute(updatedAttributes.get(2), PHONE_NUMBER, "+5556667777");
  }

  @Test
  public void testNullAttrValue() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setCellPhoneNumber("");

    UpdateDifference updateDifference = new UpdateDifference(existedCognitoUser(), userUpdate);
    List<AttributeType> updatedAttributes = buildUpdatedAttributesList(updateDifference);
    assertThat(updatedAttributes.size(), is(1));
    assertAttribute(updatedAttributes.get(0), PHONE_NUMBER, "");
  }

  private void assertAttribute(
      AttributeType attr, UserAttribute userAttribute, String value) {
    assertThat(attr, is(notNullValue()));
    assertThat(attr.getName(), is(userAttribute.getName()));
    assertThat(attr.getValue(), is(value));
  }

  private void assertCollectionAttribute(
      AttributeType attr, UserAttribute userAttribute, String... elements) {
    assertThat(attr, is(notNullValue()));
    assertThat(attr.getName(), is(userAttribute.getName()));
    String value = attr.getValue();

    for (String element : elements) {
      assertTrue(value.contains(element));
    }
  }

  private User existedCognitoUser() {
    User user = new User();
    user.setEmail("user@oci.ca.gov");
    user.setPhoneNumber("1234567890");
    user.setPhoneExtensionNumber("28");
    user.setCellPhoneNumber("2223334444");
    user.setRoles(toSet(STATE_ADMIN, COUNTY_ADMIN));
    user.setPermissions(toSet("Snapshot-rollout", "Hotline-rollout"));
    return user;
  }
}