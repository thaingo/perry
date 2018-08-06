package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.cognito.CustomUserAttribute.RACFID_CUSTOM;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.getRACFId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import org.junit.Test;

public class IdmServiceImplTest {

  @Test
  public void testGetRACFId() {
    UserType cognitoUser = new UserType();

    AttributeType attr = new AttributeType();
    attr.setName(RACFID_CUSTOM.getName());
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
