package gov.ca.cwds.idm.service.authorization;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;

public interface AuthorizationService {

  void checkCanViewUser(User user);

  void checkCanCreateUser(User user);

  void checkCanUpdateUser(UserType existingUser, UserUpdate updateUserDto);

  boolean canUpdateUser(String userId);

  void checkCanResendInvitationMessage(String id);

  boolean canEditRoles(User user);

  boolean canEditPermissions(User user);
}
