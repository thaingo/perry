package gov.ca.cwds.idm.service.role.implementor;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.util.TestHelper.superAdmin;
import static gov.ca.cwds.idm.util.TestHelper.user;
import static gov.ca.cwds.service.messages.MessageCode.CALS_ADMIN_CANNOT_VIEW_NON_CALS_USER;
import static gov.ca.cwds.service.messages.MessageCode.NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE;
import static gov.ca.cwds.service.messages.MessageCode.ROLE_IS_UNSUFFICIENT_FOR_OPERATION;
import static gov.ca.cwds.util.Utils.toSet;

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

  @Test
  public void canNotViewSuperAdmin() {
    assertCanNotViewUser(superAdmin(), CALS_ADMIN_CANNOT_VIEW_NON_CALS_USER);
  }

  @Test
  public void canNotViewSuperAdminWhichIsCalsExternalWorkerToo() {
    assertCanNotViewUser(
        user(toSet(SUPER_ADMIN, CALS_EXTERNAL_WORKER), "Yolo", "Yolo_2"),
        NOT_SUPER_ADMIN_CANNOT_VIEW_USERS_WITH_SUPER_ADMIN_ROLE);
  }

  @Test
  public void canNotCreateSuperAdmin() {
    assertCanNotCreateUser(superAdmin(), ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }

  @Test
  public void canNotUpdateSuperAdmin() {
    assertCanNotUpdateUser(superAdmin(), ROLE_IS_UNSUFFICIENT_FOR_OPERATION);
  }
}