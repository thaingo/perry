package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.User;

public interface AuthorizationService {

  boolean canViewUser(User user);

  boolean canCreateUser(User user);

  boolean canUpdateUser(String userId);

  boolean canResendInvitationMessage(String email);

}
