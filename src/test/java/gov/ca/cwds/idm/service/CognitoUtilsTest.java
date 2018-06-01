package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.CognitoUtils.COUNTY_ATTR_NAME;
import static gov.ca.cwds.idm.service.CognitoUtils.PERMISSIONS_ATTR_NAME;
import static gov.ca.cwds.idm.service.CognitoUtils.createPermissionsAttribute;
import static gov.ca.cwds.idm.service.CognitoUtils.getAttribute;
import static gov.ca.cwds.idm.service.CognitoUtils.getCountyName;
import static gov.ca.cwds.idm.service.CognitoUtils.getPermissions;
import static gov.ca.cwds.idm.service.CognitoUtils.getPermissionsAttributeValue;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;

public class CognitoUtilsTest {

  @Test
  public void testGetAttributeNoAttributes() {
    UserType cognitoUser = new UserType();
    Optional<AttributeType> attrOpt = getAttribute(cognitoUser, "someName");
    assertThat(attrOpt.isPresent(), is(false));
  }

  @Test
  public void testGetAttributeEmptyAttributes() {
    UserType cognitoUser = new UserType();
    cognitoUser.withAttributes();
    Optional<AttributeType> attrOpt = getAttribute(cognitoUser, "someName");
    assertThat(attrOpt.isPresent(), is(false));
  }

  @Test
  public void testGetAttributeOtherAttribute() {
    UserType cognitoUser = new UserType();

    AttributeType otherAttr = new AttributeType();
    otherAttr.setName("otherName");
    otherAttr.setValue("otherValue");

    cognitoUser.withAttributes(otherAttr);

    Optional<AttributeType> attrOpt = getAttribute(cognitoUser, "someName");
    assertThat(attrOpt.isPresent(), is(false));
  }

  @Test
  public void testGetAttributeValidAttribute() {
    UserType cognitoUser = new UserType();

    AttributeType validAttr = new AttributeType();
    validAttr.setName("someName");
    validAttr.setValue("someValue");

    AttributeType otherAttr = new AttributeType();
    otherAttr.setName("otherName");
    otherAttr.setValue("otherValue");

    cognitoUser.withAttributes(validAttr, otherAttr);

    Optional<AttributeType> attrOpt = getAttribute(cognitoUser, "someName");
    assertTrue(attrOpt.isPresent());
    AttributeType attr = attrOpt.get();
    assertThat(attr.getName(), is("someName"));
    assertThat(attr.getValue(), is("someValue"));

    Optional<AttributeType> attrOptUpCase = getAttribute(cognitoUser, "SOMENAME");
    assertTrue(attrOptUpCase.isPresent());
    AttributeType attrUpCase = attrOptUpCase.get();
    assertThat(attrUpCase.getName(), is("someName"));
    assertThat(attrUpCase.getValue(), is("someValue"));
  }

  @Test
  public void testGetNoPermissions() {

    UserType cognitoUser = new UserType();

    AttributeType otherAttr = new AttributeType();
    otherAttr.setName("otherName");
    otherAttr.setValue("otherValue");

    cognitoUser.withAttributes(otherAttr);

    assertThat(getPermissions(cognitoUser), empty());
  }

  @Test
  public void testGetNullPermissions() {

    UserType cognitoUser = new UserType();

    AttributeType permissionsAttr = new AttributeType();
    permissionsAttr.setName(PERMISSIONS_ATTR_NAME);
    permissionsAttr.setValue(null);

    cognitoUser.withAttributes(permissionsAttr);

    assertThat(getPermissions(cognitoUser), empty());
  }

  @Test
  public void testGetEmptyPermissions() {

    UserType cognitoUser = new UserType();

    AttributeType permissionsAttr = new AttributeType();
    permissionsAttr.setName(PERMISSIONS_ATTR_NAME);
    permissionsAttr.setValue("");

    cognitoUser.withAttributes(permissionsAttr);

    assertThat(getPermissions(cognitoUser), empty());
  }

  @Test
  public void testGetPermissions() {
    UserType cognitoUser = new UserType();

    AttributeType permissionsAttr = new AttributeType();
    permissionsAttr.setName(PERMISSIONS_ATTR_NAME);
    permissionsAttr.setValue("Snapshot-rollout:Hotline-rollout");

    cognitoUser.withAttributes(permissionsAttr);

    Set<String> permissions = getPermissions(cognitoUser);
    assertThat(permissions, hasSize(2));
    assertThat(permissions, hasItem("Snapshot-rollout"));
    assertThat(permissions, hasItem("Hotline-rollout"));
  }

  @Test
  public void testGetPermissionsAttributeValueNull() {
    assertThat(getPermissionsAttributeValue(null), is(""));
  }

  @Test
  public void testGetPermissionsAttributeValueEmpty() {
    Set<String> permissions = new HashSet<>();
    assertThat(getPermissionsAttributeValue(permissions), is(""));
  }

  @Test
  public void testGetPermissionsAttributeValue() {
    Set<String> permissions = new HashSet<>();
    permissions.add("one");
    permissions.add("two");
    assertThat(getPermissionsAttributeValue(permissions), is("one:two"));
  }

  @Test
  public void testCreatePermissionsAttribute() {
    Set<String> permissions = new HashSet<>();
    permissions.add("one");
    permissions.add("two");
    AttributeType attr = createPermissionsAttribute(permissions);
    assertThat(attr.getName(), is(PERMISSIONS_ATTR_NAME));
    assertThat(attr.getValue(), is("one:two"));
  }

  @Test
  public void testGetCountyName() {
    UserType cognitoUser = new UserType();

    AttributeType attr = new AttributeType();
    attr.setName(COUNTY_ATTR_NAME);
    attr.setValue("Yolo");
    cognitoUser.withAttributes(attr);

    assertThat(getCountyName(cognitoUser), is("Yolo"));
  }

  @Test
  public void testGetCountyNameNoCountyAttr() {
    UserType cognitoUser = new UserType();
    assertThat(getCountyName(cognitoUser), is(nullValue()));
  }

}
