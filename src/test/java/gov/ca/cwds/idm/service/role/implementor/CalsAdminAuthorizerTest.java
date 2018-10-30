package gov.ca.cwds.idm.service.role.implementor;

import static org.junit.Assert.assertFalse;

import gov.ca.cwds.idm.dto.User;
import org.junit.Test;

public class CalsAdminAuthorizerTest {

  @Test
  public void canNotEditStateAdminTest() {
    assertFalse(new CalsAdminAuthorizer(new User()).canEditRoles());
  }

}