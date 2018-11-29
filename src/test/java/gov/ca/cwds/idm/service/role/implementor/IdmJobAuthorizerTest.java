package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.idm.util.TestHelper.calsAdmin;
import static gov.ca.cwds.idm.util.TestHelper.idmJob;
import static gov.ca.cwds.idm.util.TestHelper.countyAdmin;
import static gov.ca.cwds.idm.util.TestHelper.cwsWorker;
import static gov.ca.cwds.idm.util.TestHelper.officeAdmin;
import static gov.ca.cwds.idm.util.TestHelper.stateAdmin;
import static gov.ca.cwds.service.messages.MessageCode.ROLE_IS_UNSUFFICIENT_FOR_OPERATION;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserCountyName;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.idm.dto.User;
import org.junit.Before;
import org.junit.Test;

public class IdmJobAuthorizerTest extends BaseAuthorizerTest {

  @Before
  public void mockCountyAdmin() {
    when(getCurrentUser()).thenReturn(idmJob());
    when(getCurrentUserCountyName()).thenReturn(null);
  }

  @Override
  protected AbstractAdminActionsAuthorizer getAuthorizer(User user) {
    return new IdmJobAuthorizer(user);
  }

  @Test
  public void canNotCreateUser() {
    assertCanNotEditRoles(cwsWorker(), ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }

  @Test
  public void canNotUpdateUser() {
    assertCanNotUpdateUser(cwsWorker(), ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }

  @Test
  public void canNotResendInvitationMessage() {
    assertCanNotResendInvitationMessage(cwsWorker(), ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }

  @Test
  public void canNotEditRoles() {
    assertCanNotEditRoles(cwsWorker(), ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }

  @Test
  public void canViewCwsWorker() {
    assertCanViewUser(cwsWorker());
  }

  @Test
  public void canViewOfficeAdmin() {
    assertCanViewUser(officeAdmin());
  }

  @Test
  public void canViewCountyAdmin() {
    assertCanViewUser(countyAdmin());
  }

  @Test
  public void canViewStateAdmin() {
    assertCanViewUser(stateAdmin());
  }

  @Test
  public void canViewCalsAdmin() {
    assertCanViewUser(calsAdmin());
  }
}