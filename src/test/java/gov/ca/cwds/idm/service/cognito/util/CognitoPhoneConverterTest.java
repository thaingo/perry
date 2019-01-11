package gov.ca.cwds.idm.service.cognito.util;

import static gov.ca.cwds.idm.service.cognito.util.CognitoPhoneConverter.toCognitoFormat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CognitoPhoneConverterTest {

  @Test
  public void testToCognitoFormat() {
    assertThat(toCognitoFormat(null), is(nullValue()));
    assertThat(toCognitoFormat(""), is(""));
    assertThat(toCognitoFormat(" "), is(""));
    assertThat(toCognitoFormat("+123"), is("+123"));
    assertThat(toCognitoFormat("123"), is("+123"));
  }
}
