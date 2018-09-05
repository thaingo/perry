package gov.ca.cwds.data.persistence.auth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;


public class StaffUnitAuthorityTest {

  @Test
  public void equalsHashCodeWork() {
    EqualsVerifier.forClass(StaffUnitAuthority.class).withRedefinedSuperclass().suppress(Warning.NONFINAL_FIELDS).verify();
  }

  /*
   * Constructor test
   */
  @Test
  public void emtpyConstructorIsNotNull() throws Exception {
    assertThat(StaffUnitAuthority.class.newInstance(), is(notNullValue()));
  }
}
