package gov.ca.cwds.idm.service.validate;

import static gov.ca.cwds.service.messages.MessageCode.ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM;
import static gov.ca.cwds.service.messages.MessageCode.NO_USER_WITH_RACFID_IN_CWSCMS;
import static org.mockito.Mockito.when;

import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.rest.api.domain.UserIdmValidationException;
import gov.ca.cwds.service.CwsUserInfoService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import gov.ca.cwds.service.messages.MessagesService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserActivationRuleTest {

  private static final String ACTIVE_USER_WITH_RACFID_EXISTS_IN_COGNITO_ERROR_MSG =
      "Active User with RACFID: SMITHBO exists in Cognito";
  private static final String NO_ACTIVE_USER_WITH_RACFID_IN_CMS_ERROR_MSG =
      "No user with RACFID: SMITHBO found in CWSCMS";
  private String racfId = "SMITHBO";

  @InjectMocks private UserActivationRule userActivationRule;
  @Mock protected MessagesService messages;
  @Mock protected CwsUserInfoService cwsUserInfoService;
  @Mock protected CognitoServiceFacade cognitoServiceFacade;

  @Rule public ExpectedException exception = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void performValidation_throwsNoRacfIdInCWS() {
    when(cwsUserInfoService.getCwsUserByRacfId(racfId)).thenReturn(null);
    when(messages.get(NO_USER_WITH_RACFID_IN_CWSCMS, racfId))
        .thenReturn(NO_ACTIVE_USER_WITH_RACFID_IN_CMS_ERROR_MSG);
    exception.expect(UserIdmValidationException.class);
    exception.expectMessage(NO_ACTIVE_USER_WITH_RACFID_IN_CMS_ERROR_MSG);
    userActivationRule.performValidation(racfId);
  }

  @Test
  public void performValidation_throwsActiveRacfIdAlreadyInCognito() {
    when(cwsUserInfoService.getCwsUserByRacfId(racfId)).thenReturn(new CwsUserInfo());
    when(cognitoServiceFacade.isActiveRacfIdPresent(racfId)).thenReturn(true);
    when(messages.get(ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM, racfId))
        .thenReturn(ACTIVE_USER_WITH_RACFID_EXISTS_IN_COGNITO_ERROR_MSG);
    exception.expect(UserIdmValidationException.class);
    exception.expectMessage(ACTIVE_USER_WITH_RACFID_EXISTS_IN_COGNITO_ERROR_MSG);
    userActivationRule.performValidation(racfId);
  }
}
