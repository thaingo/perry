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
import gov.ca.cwds.idm.service.diff.Differencing;
import java.util.List;
import org.junit.Test;

public class UserUpdateAttributesBuilderTest {

  @Test
  public void testNoChanges() {
    Differencing differencing = new Differencing(existedCognitoUser(), new UserUpdate());

    UserUpdateAttributesBuilder builder =
        new UserUpdateAttributesBuilder(differencing);
    List<AttributeType> updatedAttributes = builder.build();
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

    Differencing differencing = new Differencing(existedCognitoUser(), userUpdate);

    UserUpdateAttributesBuilder builder =
        new UserUpdateAttributesBuilder(differencing);
    List<AttributeType> updatedAttributes = builder.build();
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

    Differencing differencing = new Differencing(existedCognitoUser(), userUpdate);

    UserUpdateAttributesBuilder builder =
        new UserUpdateAttributesBuilder(differencing);
    List<AttributeType> updatedAttributes = builder.build();
    assertThat(updatedAttributes.size(), is(6));

    assertAttribute(updatedAttributes.get(0), EMAIL, "admin@oci.ca.gov");
    assertAttribute(updatedAttributes.get(1), EMAIL_VERIFIED, "True");
    assertAttribute(updatedAttributes.get(2), PHONE_NUMBER, "+0987654321");
    assertAttribute(updatedAttributes.get(3), PHONE_EXTENSION, "99");
    assertCollectionAttribute(updatedAttributes.get(4), PERMISSIONS, "Hotline-rollout",
        "RFA-rollout");
    assertCollectionAttribute(updatedAttributes.get(5), ROLES, "County-admin", "Office-admin");
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
    user.setRoles(toSet(STATE_ADMIN, COUNTY_ADMIN));
    user.setPermissions(toSet("Snapshot-rollout", "Hotline-rollout"));
    return user;
  }
}