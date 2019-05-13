package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import java.util.List;

public interface AuthorizationService {

  void checkCanViewUser(User user);

  void checkCanCreateUser(User user);

  void checkCanUpdateUser(User existingUser, UserUpdate updateUserDto);

  void checkCanUnlockUser(User user);

  void checkCanResendInvitationMessage(User user);

  boolean canUpdateUser(User user, UserUpdate updateUserDto);

  //only update rules which do not depend on new values are fired
  boolean canUpdateUser(User user);

  List<String> getAllowedUiRolesForUpdate(User user);
}
