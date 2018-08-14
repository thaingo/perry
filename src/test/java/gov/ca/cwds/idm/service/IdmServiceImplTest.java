package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.IdmServiceImpl.transformSearchValues;
import static gov.ca.cwds.idm.service.cognito.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.StandardUserAttribute.FIRST_NAME;
import static gov.ca.cwds.idm.service.cognito.StandardUserAttribute.RACFID_STANDARD;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class IdmServiceImplTest {

  @Test
  public void testTransformSearchValues() {
    assertThat(
        transformSearchValues(toSet("ROOBLA", "roobla", "Roobla"), RACFID_STANDARD),
        is(toSet("ROOBLA")));
    assertThat(
        transformSearchValues(toSet("some@email.com", "SOME@EMAIL.COM", "Some@email.com"), EMAIL),
        is(toSet("some@email.com")));
    assertThat(
        transformSearchValues(toSet("John", "JOHN", "john"), FIRST_NAME),
        is(toSet("John", "JOHN", "john")));
  }
}
