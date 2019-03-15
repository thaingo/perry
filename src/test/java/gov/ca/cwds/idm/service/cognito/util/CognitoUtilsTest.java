package gov.ca.cwds.idm.service.cognito.util;

import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.RACFID_STANDARD;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.getAttribute;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.getRACFId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import java.util.Optional;
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
  public void testAttribute() {
    AttributeType attr = new AttributeType().withName("attrName").withValue("attrValue");
    assertThat(attr, is(notNullValue()));
    assertThat(attr.getName(), is("attrName"));
    assertThat(attr.getValue(), is("attrValue"));
  }

  @Test
  public void testGetRACFId() {
    UserType cognitoUser = new UserType();

    AttributeType attr = new AttributeType();
    attr.setName(RACFID_STANDARD.getName());
    attr.setValue("YOLOD");
    cognitoUser.withAttributes(attr);

    assertThat(getRACFId(cognitoUser), is("YOLOD"));
  }

  @Test
  public void testGetRACFIdNoRACFIdAttr() {
    UserType cognitoUser = new UserType();
    assertThat(getRACFId(cognitoUser), is(nullValue()));
  }
}