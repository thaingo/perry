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
}