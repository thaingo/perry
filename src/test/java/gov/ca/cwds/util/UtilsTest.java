package gov.ca.cwds.util;

import org.junit.Test;
import static gov.ca.cwds.util.Utils.*;
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
}