package gov.ca.cwds.rest.api.domain.auth;

import org.junit.Test;


import static org.junit.Assert.*;

/**
 * CWDS API Team
 */
public class GovernmentEntityTypeTest {

  @Test
  public void testFindBySysId() throws Exception {
     assertEquals("17", GovernmentEntityType.findBySysId(1084).getCountyCd());
  }

  @Test
  public void testFindByCountyCd() throws Exception {
    assertEquals(1084, GovernmentEntityType.findByCountyCd("17").getSysId());
  }
}