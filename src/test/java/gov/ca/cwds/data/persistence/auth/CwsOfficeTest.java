package gov.ca.cwds.data.persistence.auth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;


public class CwsOfficeTest {

  @Test
  public void equalsHashCodeWork() {
    EqualsVerifier.forClass(CwsOffice.class).withRedefinedSuperclass().suppress(Warning.NONFINAL_FIELDS).verify();
  }

  /*
   * Constructor test
   */
  @Test
  public void emtpyConstructorIsNotNull() throws Exception {
    assertThat(CwsOffice.class.newInstance(), is(notNullValue()));
  }
}
