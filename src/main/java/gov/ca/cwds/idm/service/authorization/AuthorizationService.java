package gov.ca.cwds.idm.service.authorization;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserEditDetails;
import gov.ca.cwds.idm.dto.UserUpdate;

public interface AuthorizationService {

  void checkCanViewUser(User user);

  void checkCanCreateUser(User user);

  void checkCanUpdateUser(UserType existingUser, UserUpdate updateUserDto);

  void checkCanResendInvitationMessage(String id);

  UserEditDetails getEditDetails(User user);
}
