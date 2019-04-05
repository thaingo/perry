package gov.ca.cwds.idm.dto;

import static gov.ca.cwds.idm.dto.NotificationType.USER_LOCKED;
import static gov.ca.cwds.idm.dto.NotificationType.forString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.Test;

public class NotificationTypeTest {

  @Test
  public void testForString() {
    assertThat(forString("locked"), is(USER_LOCKED));
    assertThat(forString("LOCKED"), is(USER_LOCKED));
    assertThat(forString("foo"), is(nullValue()));
  }
}
