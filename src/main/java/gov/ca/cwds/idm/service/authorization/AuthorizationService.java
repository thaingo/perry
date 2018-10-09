package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;

public interface AuthorizationService {

  boolean canViewUser(User user);

  boolean canCreateUser(User user);

  boolean canUpdateUser(String userId, UserUpdate userUpdate);

  boolean canResendInvitationMessage(String userId);

}
