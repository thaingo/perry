package gov.ca.cwds.idm.service.authorization;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;

public interface AuthorizationService {

  boolean canViewUser(User user);

  boolean canCreateUser(User user);

  boolean canUpdateUser(UserType existingUser);

  boolean canUpdateUser(String userId);

  boolean canResendInvitationMessage(String email);

  boolean canEditRoles(User user);

  boolean canEditRoles(UserType cognitoUser);

}
