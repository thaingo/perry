package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PHONE_EXTENSION;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.PHONE_NUMBER;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.diff.UserAttributeDiff;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class UpdatedAttributesBuilderTest {

  @Test
  public void testNoChanges() {
    UpdatedAttributesBuilder builder =
        new UpdatedAttributesBuilder(existedCognitoUser(), new UserUpdate());
    Map<UserAttribute, UserAttributeDiff> updatedAttributes =  builder.buildUpdatedAttributesMap();
    assertThat(updatedAttributes.size(), is(0));
  }

  @Test
  public void testTheSameValues() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail("user@oci.ca.gov");
    userUpdate.setPhoneNumber("1234567890");
    userUpdate.setPhoneExtensionNumber("28");
    userUpdate.setRoles(toSet("State-admin", "County-admin"));
    userUpdate.setPermissions(toSet("Snapshot-rollout", "Hotline-rollout"));

    UpdatedAttributesBuilder builder =
        new UpdatedAttributesBuilder(existedCognitoUser(), userUpdate);
    Map<UserAttribute, UserAttributeDiff> updatedAttributes =  builder.buildUpdatedAttributesMap();
    assertThat(updatedAttributes.size(), is(0));
  }

  @Test
  public void testDifferentValues() {

    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail("admin@oci.ca.gov");
    userUpdate.setPhoneNumber("0987654321");
    userUpdate.setPhoneExtensionNumber("99");
    userUpdate.setRoles(toSet("County-admin", "Office-admin"));
    userUpdate.setPermissions(toSet("Hotline-rollout", "RFA-rollout"));

    UpdatedAttributesBuilder builder =
        new UpdatedAttributesBuilder(existedCognitoUser(), userUpdate);
    Map<UserAttribute, UserAttributeDiff> updatedAttributes =  builder.buildUpdatedAttributesMap();
    assertThat(updatedAttributes.size(), is(5));

    assertAttribute(updatedAttributes, EMAIL, "admin@oci.ca.gov");
    assertAttribute(updatedAttributes, PHONE_NUMBER, "+0987654321");
    assertAttribute(updatedAttributes, PHONE_EXTENSION, "99");
    assertCollectionAttribute(updatedAttributes, ROLES, "County-admin", "Office-admin");
    assertCollectionAttribute(updatedAttributes, PERMISSIONS, "Hotline-rollout", "RFA-rollout");
  }

  @Test
  public void testBuildEmailAttributesNullEmail() {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail(null);
    UpdatedAttributesBuilder builder =
        new UpdatedAttributesBuilder(existedCognitoUser(), userUpdate);
    Map<UserAttribute, UserAttributeDiff> updatedAttributes =  builder.buildUpdatedAttributesMap();
    assertThat(updatedAttributes.size(), CoreMatchers.is(0));
  }

  @Test
  public void testBuildEmailAttribute() {
    testEmailAttribute("new@e.mail");
  }

  @Test
  public void testBuildEmptyEmailAttribute() {
    testEmailAttribute("");
  }

  private void testEmailAttribute(String newEmail) {
    UserUpdate userUpdate = new UserUpdate();
    userUpdate.setEmail(newEmail);
    UpdatedAttributesBuilder builder =
        new UpdatedAttributesBuilder(existedCognitoUser(), userUpdate);
    Map<UserAttribute, UserAttributeDiff> updatedAttributes =  builder.buildUpdatedAttributesMap();
    assertEmailAttributes(updatedAttributes, newEmail);
  }

  private void assertEmailAttributes(Map<UserAttribute, UserAttributeDiff> attrMap, String email) {
    assertThat(attrMap, CoreMatchers.is(notNullValue()));
    assertThat(attrMap.size(), CoreMatchers.is(1));

    assertThat(attrMap.get(EMAIL), CoreMatchers.is(notNullValue()));
    AttributeType emailAttr = (AttributeType) attrMap.get(EMAIL).createAttributeTypes().get(0);
    assertThat(emailAttr.getName(), CoreMatchers.is(EMAIL.getName()));
    assertThat(emailAttr.getValue(), CoreMatchers.is(email));

    AttributeType emailVerifiedAttr = (AttributeType) attrMap.get(EMAIL).createAttributeTypes().get(1);
    assertThat(emailVerifiedAttr.getName(), CoreMatchers.is(EMAIL_VERIFIED.getName()));
    assertThat(emailVerifiedAttr.getValue(), CoreMatchers.is("True"));
  }

  private User existedCognitoUser() {
    User user = new User();
    user.setEmail("user@oci.ca.gov");
    user.setPhoneNumber("1234567890");
    user.setPhoneExtensionNumber("28");
    user.setRoles(toSet(STATE_ADMIN, COUNTY_ADMIN));
    user.setPermissions(toSet("Snapshot-rollout", "Hotline-rollout"));
    return user;
  }

  private void assertAttribute(
      Map<UserAttribute, UserAttributeDiff> updatedAttributes, UserAttribute userAttribute, String value) {
    AttributeType attr = (AttributeType) updatedAttributes.get(userAttribute).createAttributeTypes().get(0);
    assertThat(attr, is(notNullValue()));
    assertThat(attr.getName(), is(userAttribute.getName()));
    assertThat(attr.getValue(), is(value));
  }

  private void assertCollectionAttribute(
      Map<UserAttribute, UserAttributeDiff> updatedAttributes, UserAttribute userAttribute, String... elements) {
    AttributeType attr = (AttributeType) updatedAttributes.get(userAttribute).createAttributeTypes().get(0);
    assertThat(attr, is(notNullValue()));
    assertThat(attr.getName(), is(userAttribute.getName()));
    String value = attr.getValue();

    for(String element : elements) {
      assertTrue(value.contains(element));
    }
  }
}
