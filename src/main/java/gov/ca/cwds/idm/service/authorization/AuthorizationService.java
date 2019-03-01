package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;

public interface AuthorizationService {

  void checkCanViewUser(User user);

  void checkCanCreateUser(User user);

  void checkCanUpdateUser(User existingUser, UserUpdate updateUserDto);

  void checkCanUnlockUser(User user);

  void checkCanResendInvitationMessage(User user);

  boolean canUpdateUser(User user);

  boolean canEditRoles(User user);

  boolean canEditPermissions(User user);
}
