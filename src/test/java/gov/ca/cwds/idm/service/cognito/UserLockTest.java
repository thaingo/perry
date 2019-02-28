package gov.ca.cwds.idm.service.cognito;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.service.cognito.TestCognitoServiceFacade.LOCKED_USER;
import static gov.ca.cwds.idm.service.cognito.TestCognitoServiceFacade.UNLOCKED_USER;
import static org.junit.Assert.assertFalse;

import gov.ca.cwds.idm.BaseIdmIntegrationWithUserLogTest;
import gov.ca.cwds.idm.dto.UserLockedStatus;
import gov.ca.cwds.idm.util.TestUtils;
import gov.ca.cwds.idm.util.WithMockCustomUser;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional(value = TOKEN_TRANSACTION_MANAGER)
public class UserLockTest extends BaseIdmIntegrationWithUserLogTest {

  @Test
  @WithMockCustomUser(
      roles = {STATE_ADMIN},
      county = "Madera")
  public void testUnlockUserWithStateAdmin() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/idm/users/" + LOCKED_USER + "/lock"))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  @Test
  @WithMockCustomUser(
      roles = {STATE_ADMIN},
      county = "Madera")
  public void testGetLockStatusWithStateAdmin() throws Exception {
    final MvcResult mvcResult =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/users/" + UNLOCKED_USER + "/lock"))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn();
    String strResponse = mvcResult.getResponse().getContentAsString();
    UserLockedStatus userLockedStatus = TestUtils.deserialize(strResponse, UserLockedStatus.class);
    assertFalse(userLockedStatus.isLocked());
  }
}
