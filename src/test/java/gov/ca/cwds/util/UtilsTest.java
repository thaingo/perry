package gov.ca.cwds.util;

import org.junit.Test;
import org.springframework.boot.actuate.health.Status;

import static gov.ca.cwds.util.Utils.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

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
}
