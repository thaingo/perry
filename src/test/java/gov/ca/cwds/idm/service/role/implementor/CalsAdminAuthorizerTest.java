package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.service.messages.MessageCode.ROLE_IS_UNSUFFICIENT_FOR_OPERATION;

import gov.ca.cwds.idm.dto.User;
import org.junit.Test;

public class CalsAdminAuthorizerTest extends BaseAuthorizerTest {

  @Override
  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return new CalsAdminAuthorizer(user);
  }

  @Test
  public void canNotEditStateAdminTest() {
    assertCanNotEditRoles(new User(), ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }
}