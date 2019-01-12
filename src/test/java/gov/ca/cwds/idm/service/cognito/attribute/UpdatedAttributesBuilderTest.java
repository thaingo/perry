package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PHONE_EXTENSION;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.PHONE_NUMBER;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.attribute;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.UserUpdate;
import java.util.Map;
import org.junit.Test;

public class UpdatedAttributesBuilderTest {

  @Test
  public void testNoChanges() {
    UpdatedAttributesBuilder builder =
        new UpdatedAttributesBuilder(existedCognitoUser(), new UserUpdate());
    Map<UserAttribute, AttributeType> updatedAttributes =  builder.getUpdatedAttributes();
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
    Map<UserAttribute, AttributeType> updatedAttributes =  builder.getUpdatedAttributes();
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
    Map<UserAttribute, AttributeType> updatedAttributes =  builder.getUpdatedAttributes();
    assertThat(updatedAttributes.size(), is(6));

    assertAttribute(updatedAttributes, EMAIL, "admin@oci.ca.gov");
    assertAttribute(updatedAttributes, EMAIL_VERIFIED, "True");
    assertAttribute(updatedAttributes, PHONE_NUMBER, "+0987654321");
    assertAttribute(updatedAttributes, PHONE_EXTENSION, "99");
    assertCollectionAttribute(updatedAttributes, ROLES, "County-admin", "Office-admin");
    assertCollectionAttribute(updatedAttributes, PERMISSIONS, "Hotline-rollout", "RFA-rollout");
  }

  private UserType existedCognitoUser() {
    UserType userType = new UserType();

    userType.withAttributes(
        attribute(EMAIL, "user@oci.ca.gov"),
        attribute(EMAIL_VERIFIED, "True"),
        attribute(PHONE_NUMBER, "+1234567890"),
        attribute(PHONE_EXTENSION, "28"),
        attribute(ROLES, "State-admin:County-admin"),
        attribute(PERMISSIONS, "Snapshot-rollout:Hotline-rollout"));

    return userType;
  }

  private void assertAttribute(
      Map<UserAttribute, AttributeType> updatedAttributes, UserAttribute userAttribute, String value) {
    AttributeType attr = updatedAttributes.get(userAttribute);
    assertThat(attr, is(notNullValue()));
    assertThat(attr.getName(), is(userAttribute.getName()));
    assertThat(attr.getValue(), is(value));
  }

  private void assertCollectionAttribute(
      Map<UserAttribute, AttributeType> updatedAttributes, UserAttribute userAttribute, String... elements) {
    AttributeType attr = updatedAttributes.get(userAttribute);
    assertThat(attr, is(notNullValue()));
    assertThat(attr.getName(), is(userAttribute.getName()));
    String value = attr.getValue();

    for(String element : elements) {
      assertTrue(value.contains(element));
    }
  }
}
