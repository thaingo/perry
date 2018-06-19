package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.CognitoIdmService.getRACFId;
import static gov.ca.cwds.idm.service.CognitoUtils.RACFID_ATTR_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import org.junit.Test;

public class CognitoIdmServiceTest {

  @Test
  public void testGetRACFId() {
    UserType cognitoUser = new UserType();

    AttributeType attr = new AttributeType();
    attr.setName(RACFID_ATTR_NAME);
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
