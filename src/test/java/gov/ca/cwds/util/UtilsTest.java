package gov.ca.cwds.util;

import static gov.ca.cwds.idm.service.cognito.CustomUserAttribute.RACFID_CUSTOM;
import static gov.ca.cwds.util.Utils.healthCheckUtcTimeToPacific;
import static gov.ca.cwds.util.Utils.isRacfidUser;
import static gov.ca.cwds.util.Utils.isStatusHealthy;
import static gov.ca.cwds.util.Utils.toCommaDelimitedString;
import static gov.ca.cwds.util.Utils.toLowerCase;
import static gov.ca.cwds.util.Utils.toUpperCase;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import org.springframework.boot.actuate.health.Status;

public class UtilsTest {

  @Test
  public void testToUpperCase() {
    assertThat(toUpperCase(null), is(nullValue()));
    assertThat(toUpperCase("abc"), is("ABC"));
    assertThat(toUpperCase("Abc"), is("ABC"));
    assertThat(toUpperCase("ABC"), is("ABC"));
  }

  @Test
  public void testToLowerCase() {
    assertThat(toLowerCase(null), is(nullValue()));
    assertThat(toLowerCase("gonzales@gmail.com"), is("gonzales@gmail.com"));
    assertThat(toLowerCase("Gonzales@Gmail.Com"), is("gonzales@gmail.com"));
    assertThat(toLowerCase("GONZALES@GMAIL.COM"), is("gonzales@gmail.com"));
  }

  @Test
  public void testIsRacfidUser() {
    assertTrue(isRacfidUser(user("SOMERACFID")));
    assertFalse(isRacfidUser(user(null)));
    assertFalse(isRacfidUser(user("")));
    assertFalse(isRacfidUser(user(" ")));
  }

  @Test
  public void testIsRacfidCognitoUser() {
    UserType cognitoUser = new UserType();
    assertFalse(isRacfidUser(cognitoUser));

    AttributeType racfidAttr = new AttributeType();
    racfidAttr.setName(RACFID_CUSTOM.getName());
    racfidAttr.setValue("");
    cognitoUser.withAttributes(racfidAttr);
    assertFalse(isRacfidUser(cognitoUser));

    racfidAttr.setValue("SOMERACFID");
    cognitoUser.withAttributes(racfidAttr);
    assertTrue(isRacfidUser(cognitoUser));
  }

  private User user(String rachfid) {
    User user = new User();
    user.setRacfid(rachfid);
    return user;
  }

  @Test
  public void testIsHealthy() {
    assertThat(isStatusHealthy(Status.UP), is(true));
    assertThat(isStatusHealthy(Status.DOWN), is(false));
    assertThat(isStatusHealthy(Status.OUT_OF_SERVICE), is(false));
    assertThat(isStatusHealthy(Status.UNKNOWN), is(false));
  }

  @Test
  public void testHealthCheckUtcTimeToPacific() {
    assertThat(healthCheckUtcTimeToPacific("2018-10-22 22:59:43+0000"),
        is(equalTo("2018-10-22T15:59:43.000-07:00")));
  }

  @Test
  public void testToCommaDelimitedString() {
    assertThat(toCommaDelimitedString(null), nullValue());
    assertThat(toCommaDelimitedString(new ArrayList<>()), is(equalTo("[]")));
    assertThat(toCommaDelimitedString(Arrays.asList("one")), is(equalTo("[one]")));
    assertThat(toCommaDelimitedString(Arrays.asList("one", "two")), is(equalTo("[one, two]")));
  }
}
