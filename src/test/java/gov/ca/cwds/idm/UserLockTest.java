package gov.ca.cwds.idm;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.LOCKED_USER;

import gov.ca.cwds.idm.util.WithMockCustomUser;
import org.junit.Test;
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
}
