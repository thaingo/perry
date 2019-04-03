package gov.ca.cwds.idm.lifecycle;

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

  public void unlockUser(String userId) {
    User existedUser = new User();
    existedUser.setCountyName("county");
    existedUser.setId("id1");
    existedUser.setFirstName("TESTName");
    existedUser.setLastName("TESTLASTNAME");

    auditService.saveAuditEvent(new UserUnlockedEvent(existedUser));
  }
}
