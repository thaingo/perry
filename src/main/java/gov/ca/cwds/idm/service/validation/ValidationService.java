package gov.ca.cwds.idm.service.validation;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;

public interface ValidationService {

  void validateUserCreate(User enrichedUser, boolean activeUserExistsInCws);

  void validateVerifyIfUserCanBeCreated(User enrichedUser, boolean activeUserExistsInCws);

  void validateUserUpdate(User existedUser, UserUpdate updateUserDto);
}
