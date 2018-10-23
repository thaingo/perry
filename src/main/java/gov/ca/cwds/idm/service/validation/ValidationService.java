package gov.ca.cwds.idm.service.validation;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;

public interface ValidationService {

  void validateUserCreate(User enrichedUser, boolean activeUserExistsInCws);

  void validateVerifyIfUserCanBeCreated(User enrichedUser, boolean activeUserExistsInCws);

  void validateUpdateUser(UserType existedCognitoUser, UserUpdate updateUserDto);
}
