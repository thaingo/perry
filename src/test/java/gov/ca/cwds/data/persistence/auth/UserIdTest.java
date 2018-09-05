package gov.ca.cwds.data.persistence.auth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;


public class UserIdTest {

  @Test
  public void equalsHashCodeWork() {
    EqualsVerifier.forClass(UserId.class).withRedefinedSuperclass().suppress(Warning.NONFINAL_FIELDS).verify();
  }

  /*
   * Constructor test
   */
  @Test
  public void emtpyConstructorIsNotNull() throws Exception {
    assertThat(UserId.class.newInstance(), is(notNullValue()));
  }
}
