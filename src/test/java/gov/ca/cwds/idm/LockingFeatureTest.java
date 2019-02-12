package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_FIVE_LOGIN_FAILURES_LOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_NO_LOGIN_FAILURE_UNLOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_ONE_LOGIN_FAILURE_UNLOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_THREE_LOGIN_FAILURES_LOCKED;

import gov.ca.cwds.idm.util.WithMockCustomUser;
import org.junit.Test;

public class LockingFeatureTest extends BaseIdmIntegrationTest {


  @Test
  @WithMockCustomUser
  public void testGetUserOneFailedLoginShouldBeUnlocked() throws Exception {
    testGetValidUser(USER_WITH_ONE_LOGIN_FAILURE_UNLOCKED,
        "fixtures/idm/get-user/one-failed-login-unlocked.json");
  }

  //no login failure value available
  @Test
  @WithMockCustomUser
  public void testGetUserNoFailedLoginValueShouldBeUnlocked() throws Exception {
    testGetValidUser(USER_WITH_NO_LOGIN_FAILURE_UNLOCKED,
        "fixtures/idm/get-user/no-failed-login-value-unlocked.json");
  }

  //more then 3 login failure
  @Test
  @WithMockCustomUser
  public void testGetUserFiveFailedLoginShouldBeLocked() throws Exception {
    testGetValidUser(USER_WITH_FIVE_LOGIN_FAILURES_LOCKED,
        "fixtures/idm/get-user/five-failed-login-locked.json");
  }

  //3 login failure
  @Test
  @WithMockCustomUser
  public void testGetUserThreeFailedLoginShouldBeLocked() throws Exception {
    testGetValidUser(USER_WITH_THREE_LOGIN_FAILURES_LOCKED,
        "fixtures/idm/get-user/three-failed-login-locked.json");
  }

}
