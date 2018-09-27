package gov.ca.cwds.idm.service.validate;

import static gov.ca.cwds.service.messages.MessageCode.ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM;
import static gov.ca.cwds.service.messages.MessageCode.NO_USER_WITH_RACFID_IN_CWSCMS;

import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.rest.api.domain.UserIdmValidationException;
import gov.ca.cwds.service.CwsUserInfoService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import gov.ca.cwds.service.messages.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Validates all rules when changing enable status of user from Inactive to Active status. */
@Component
@Profile("idm")
public class UserActivationRule implements ValidationRule {
  @Autowired private MessagesService messages;
  @Autowired private CwsUserInfoService cwsUserInfoService;
  @Autowired private CognitoServiceFacade cognitoServiceFacade;

  @Override
  public void performValidation(String racfId) {
    CwsUserInfo cwsUser = cwsUserInfoService.getCwsUserByRacfId(racfId);
    // validates users not active in CWS cannot be set to Active in CWS CARES
    if (cwsUser == null) {
      String msg = messages.get(NO_USER_WITH_RACFID_IN_CWSCMS, racfId);
      throw new UserIdmValidationException(NO_USER_WITH_RACFID_IN_CWSCMS, msg);
    }

    // validates no other Active users with same RACFID in CWS CARES exist
    if (cognitoServiceFacade.isActiveRacfIdPresent(racfId)) {
      String msg = messages.get(ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM, racfId);
      throw new UserIdmValidationException(ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM, msg);
    }
  }
}
