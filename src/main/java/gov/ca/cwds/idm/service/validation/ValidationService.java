package gov.ca.cwds.idm.service.validation;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;

public interface ValidationService {

  void validateUserCreate(User enrichedUser);

  void validateVerifyIfUserCanBeCreated(User enrichedUser);

  void validateUserUpdate(User existedUser, UserUpdate updateUserDto);

  void validateUnlockUser(User existedUser, boolean newLocked);
}
