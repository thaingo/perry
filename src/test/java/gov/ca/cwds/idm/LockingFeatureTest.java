package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_MORE_THEN_MAX_FAILURES_LOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_NO_LOGIN_FAILURE_UNLOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_LESS_THEN_MAX_LOGIN_FAILURES_UNLOCKED;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_EXACT_NUMBER_LOGIN_FAILURES_LOCKED;

import gov.ca.cwds.idm.util.WithMockCustomUser;
import org.junit.Test;

public class LockingFeatureTest extends BaseIdmIntegrationTest {


  @Test
  @WithMockCustomUser
  public void testGetUserLessThenMaxFailedLoginsShouldBeUnlocked() throws Exception {
    testGetValidUser(USER_WITH_LESS_THEN_MAX_LOGIN_FAILURES_UNLOCKED,
        "fixtures/idm/get-user/less-then-max-failed-logins-unlocked.json");
  }

  //no login failure value available
  @Test
  @WithMockCustomUser
  public void testGetUserNoFailedLoginValueShouldBeUnlocked() throws Exception {
    testGetValidUser(USER_WITH_NO_LOGIN_FAILURE_UNLOCKED,
        "fixtures/idm/get-user/no-failed-login-value-unlocked.json");
  }

  //more then max failed logins to lock
  @Test
  @WithMockCustomUser
  public void testGetUserMoreThanMaxFailedLoginShouldBeLocked() throws Exception {
    testGetValidUser(USER_WITH_MORE_THEN_MAX_FAILURES_LOCKED,
        "fixtures/idm/get-user/more-then-max-failed-logins-locked.json");
  }

  //exact number of failed logins to lock
  @Test
  @WithMockCustomUser
  public void testGetUserExactNumberOfFailedLoginShouldBeLocked() throws Exception {
    testGetValidUser(USER_WITH_EXACT_NUMBER_LOGIN_FAILURES_LOCKED,
        "fixtures/idm/get-user/exact-number-failed-logins-locked.json");
  }

}
