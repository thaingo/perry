package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.LOCKED_USER;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.UNLOCKED_USER;
import static gov.ca.cwds.idm.util.TestCognitoServiceFacade.USER_WITH_NO_LOCKED_VALUE_UNLOCKED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesResult;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.UserUnlockedEvent;
import gov.ca.cwds.idm.service.search.UserSearchService;
import gov.ca.cwds.idm.util.WithMockCustomUser;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class UserLockTest extends BaseIdmIntegrationWithSearchTest {

  @Test
  @WithMockCustomUser(county = "OtherCounty")
  public void testUnlockUserErrorUnauthorized() throws Exception {
    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.delete("/idm/users/" + LOCKED_USER + "/lock"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andReturn();
    verify(cognito, times(0)).adminUpdateUserAttributes(getUnlockUserUpdateRequest(LOCKED_USER));
    verify(auditEventService, never()).saveAuditEvent(any(UserUnlockedEvent.class));
    assertExtensible(
        result, "fixtures/idm/user-lock-status/unlock-user-with-different-county.json");
  }

  @Test
  @WithMockCustomUser
  public void testValidationAlreadyUnlockedUser() throws Exception {
    assertUnlockUserBadRequest(
        UNLOCKED_USER, "fixtures/idm/user-lock-status/unlock-user-error.json");
  }

  @Test
  @WithMockCustomUser
  public void testValidationUnlockedUserWithNullLockedValue() throws Exception {
    assertUnlockUserBadRequest(
        USER_WITH_NO_LOCKED_VALUE_UNLOCKED, "fixtures/idm/user-lock-status/unlock-user-error.json");
  }

  @Test
  @WithMockCustomUser(
      roles = {STATE_ADMIN},
      county = "Madera")
  public void testUnlockUserHappyPath() throws Exception {
    when(cognito.adminUpdateUserAttributes(
        cognitoRequestHelper.getAdminUpdateUserAttributesRequest(
                LOCKED_USER, cognitoRequestHelper.getLockedAttributeType())))
        .thenReturn(new AdminUpdateUserAttributesResult());

    UserSearchService userSearchService = mock(UserSearchService.class);
    userLockService.setUserSearchService(userSearchService);

    mockMvc
        .perform(MockMvcRequestBuilders.delete("/idm/users/" + LOCKED_USER + "/lock"))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    verify(cognito, times(1)).adminUpdateUserAttributes(getUnlockUserUpdateRequest(LOCKED_USER));
    verify(auditEventService, times(1)).saveAuditEvent(any(UserUnlockedEvent.class));
    verify(userSearchService, times(1)).updateUserInSearch(any(User.class));
  }

  private void assertUnlockUserBadRequest(String userId, String fixture) throws Exception {
    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.delete("/idm/users/" + userId + "/lock"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
    verify(cognito, times(0)).adminUpdateUserAttributes(getUnlockUserUpdateRequest(userId));
    verify(auditEventService, never()).saveAuditEvent(any(UserUnlockedEvent.class));
    assertExtensible(result, fixture);
  }

  private AdminUpdateUserAttributesRequest getUnlockUserUpdateRequest(String lockedUserId) {
    return cognitoRequestHelper.getAdminUpdateUserAttributesRequest(
        lockedUserId, cognitoRequestHelper.getLockedAttributeType());
  }
}
