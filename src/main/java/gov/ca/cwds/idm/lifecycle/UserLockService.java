package gov.ca.cwds.idm.lifecycle;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.UserUnlockedEvent;
import gov.ca.cwds.idm.service.AuditEventService;
import gov.ca.cwds.idm.service.UserService;
import gov.ca.cwds.idm.service.authorization.AuthorizationService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "userLockService")
@Profile("idm")
public class UserLockService {

  @Autowired
  private CognitoServiceFacade cognitoServiceFacade;

  @Autowired
  private AuthorizationService authorizeService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private UserService userService;

  @Autowired
  private AuditEventService auditService;

  @Transactional(TOKEN_TRANSACTION_MANAGER)
  public void unlockUser(String userId) {
    User existedUser = userService.getUser(userId);
    authorizeService.checkCanUnlockUser(existedUser);
    validationService.validateUnlockUser(existedUser, false);

    cognitoServiceFacade.unlockUser(userId);
    auditService.saveAuditEvent(new UserUnlockedEvent(existedUser));
  }
}
