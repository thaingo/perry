package gov.ca.cwds.idm.service.cognito.attribute;

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
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class UpdatedAttributesBuilderTest {

  @Test
  public void testNoChanges() {
    UpdatedAttributesBuilder builder =
        new UpdatedAttributesBuilder(existedCognitoUser(), new UserUpdate());
    Map<UserAttribute, UserAttributeDiff> updatedAttributes =  builder.getUpdatedAttributes();
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
    Map<UserAttribute, UserAttributeDiff> updatedAttributes =  builder.getUpdatedAttributes();
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
    Map<UserAttribute, UserAttributeDiff> updatedAttributes =  builder.getUpdatedAttributes();
    assertThat(updatedAttributes.size(), is(6));

    assertAttribute(updatedAttributes, EMAIL, "admin@oci.ca.gov");
    assertAttribute(updatedAttributes, EMAIL_VERIFIED, "True");
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
    Map<UserAttribute, UserAttributeDiff> updatedAttributes =  builder.getUpdatedAttributes();
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
    Map<UserAttribute, UserAttributeDiff> updatedAttributes =  builder.getUpdatedAttributes();
    assertEmailAttributes(updatedAttributes, newEmail);
  }

  private void assertEmailAttributes(Map<UserAttribute, UserAttributeDiff> attrMap, String email) {
    assertThat(attrMap, CoreMatchers.is(notNullValue()));
    assertThat(attrMap.size(), CoreMatchers.is(2));

    assertThat(attrMap.get(EMAIL), CoreMatchers.is(notNullValue()));
    AttributeType emailAttr = attrMap.get(EMAIL).getAttributeType();
    assertThat(emailAttr.getName(), CoreMatchers.is(EMAIL.getName()));
    assertThat(emailAttr.getValue(), CoreMatchers.is(email));

    assertThat(attrMap.get(EMAIL_VERIFIED), CoreMatchers.is(notNullValue()));
    AttributeType emailVerifiedAttr = attrMap.get(EMAIL_VERIFIED).getAttributeType();
    assertThat(emailVerifiedAttr.getName(), CoreMatchers.is(EMAIL_VERIFIED.getName()));
    assertThat(emailVerifiedAttr.getValue(), CoreMatchers.is("True"));
  }

  private UserType existedCognitoUser() {
    UserType userType = new UserType();

    userType.withAttributes(
        new AttributeType().withName(((UserAttribute) EMAIL).getName())
            .withValue("user@oci.ca.gov"),
        new AttributeType().withName(((UserAttribute) EMAIL_VERIFIED).getName()).withValue("True"),
        new AttributeType().withName(((UserAttribute) PHONE_NUMBER).getName())
            .withValue("+1234567890"),
        new AttributeType().withName(((UserAttribute) PHONE_EXTENSION).getName()).withValue("28"),
        new AttributeType().withName(((UserAttribute) ROLES).getName())
            .withValue("State-admin:County-admin"),
        new AttributeType().withName(((UserAttribute) PERMISSIONS).getName())
            .withValue("Snapshot-rollout:Hotline-rollout"));

    return userType;
  }

  private void assertAttribute(
      Map<UserAttribute, UserAttributeDiff> updatedAttributes, UserAttribute userAttribute, String value) {
    AttributeType attr = updatedAttributes.get(userAttribute).getAttributeType();
    assertThat(attr, is(notNullValue()));
    assertThat(attr.getName(), is(userAttribute.getName()));
    assertThat(attr.getValue(), is(value));
  }

  private void assertCollectionAttribute(
      Map<UserAttribute, UserAttributeDiff> updatedAttributes, UserAttribute userAttribute, String... elements) {
    AttributeType attr = updatedAttributes.get(userAttribute).getAttributeType();
    assertThat(attr, is(notNullValue()));
    assertThat(attr.getName(), is(userAttribute.getName()));
    String value = attr.getValue();

    for(String element : elements) {
      assertTrue(value.contains(element));
    }
  }
}
